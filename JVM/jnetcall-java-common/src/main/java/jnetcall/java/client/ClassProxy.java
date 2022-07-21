package jnetcall.java.client;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.IExecutor;
import jnetbase.java.threads.ManualResetEvent;
import jnetbase.java.threads.Tasks;
import jnetcall.java.api.flow.ICall;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.flow.MethodStatus;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.IPushTransport;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.api.IProxy;
import jnetcall.java.client.model.CallState;
import jnetcall.java.client.model.DelegateRef;
import jnetcall.java.impl.util.ClassTools;
import jnetproto.java.tools.Conversions;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClassProxy implements IProxy {

    private final IExecutor _executor;
    private final ISendTransport _protocol;
    private final ConcurrentMap<Short, CallState> _signals;

    private boolean _running;

    public ClassProxy(ISendTransport protocol, IExecutor executor) {
        _executor = executor;
        _protocol = protocol;
        _signals = new ConcurrentHashMap<Short, CallState>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var call = pack(method, args);
        if (call == null)
            return null;
        var answer = request(method, call);
        return answer;
    }

    private static AtomicInteger _callId = new AtomicInteger();

    private static int getNextId() {
        return _callId.incrementAndGet();
    }

    private MethodCall pack(Method method, Object[] rawArgs) throws Exception {
        var id = (short) getNextId();
        var source = method.getDeclaringClass().getSimpleName();
        var args = rewriteArgsIfNeeded(rawArgs);
        var call = new MethodCall(id, source, method.getName(), args);
        if (call.C().equals("AutoCloseable") && call.M().equals("close")) {
            close();
            return null;
        }
        return call;
    }

    private static final Map<String, DelegateRef> Delegates = new HashMap<String, DelegateRef>();

    private static short wrapFromDelegate(Object del) {
        var delId = ClassTools.toDelegateId(del);
        DelegateRef delRef;
        if ((delRef = Delegates.getOrDefault(delId, null)) == null) {
            Delegates.put(delId, delRef = new DelegateRef());
            delRef.CallId = (short) getNextId();
            delRef.Entry = del;
        }
        return delRef.CallId;
    }

    private static Object[] rewriteArgsIfNeeded(Object[] raw) {
        if (raw == null) {
            return new Object[0];
        }
        var args = new Object[raw.length];
        for (var i = 0; i < args.length; i++) {
            var arg = raw[i];
            if (!Reflect.isDelegate(arg)) {
                args[i] = arg;
                continue;
            }
            args[i] = wrapFromDelegate(arg);
        }
        return args;
    }

    @Override
    public void close() throws Exception {
        _running = false;
        _executor.close();
        disposeSignals();
        _protocol.close();
    }

    private void disposeSignals() {
        for (var signal : _signals.entrySet())
            disposeSignal(signal);
        _signals.clear();
    }

    private static void disposeSignal(Map.Entry<Short, CallState> signal) {
        var state = signal.getValue();
        state.Result = new InterruptedException("Dispose");
        state.set();
    }

    private CallState createState(ICall call, boolean sync) {
        var callId = call.I();
        var state = new CallState();
        if (sync)
            state.SyncWait = new ManualResetEvent(false);
        else
            state.AsyncWait = new ManualResetEvent(false);
        _signals.put(callId, state);
        return state;
    }

    private Object waitSignal(ICall call) throws InterruptedException {
        var id = call.I();
        var state = _signals.get(id);
        state.SyncWait.waitOne();
        var res = state.Result;
        return res;
    }

    private CompletableFuture<Object> pinSignal(ICall call) {
        var id = call.I();
        var task = Tasks.wrap(() -> {
            var state = _signals.get(id);
            state.AsyncWait.waitOne();
            var res = state.Result;
            return res;
        });
        return task;
    }

    private void setSignal(ICall call) {
        if (call instanceof MethodResult mr && mr.S() == MethodStatus.Continue.getValue()) {
            setDelegate(mr);
            return;
        }
        var callId = call.I();
        var state = _signals.get(callId);
        state.Result = call;
        state.set();
    }

    private static void setDelegate(MethodResult msg) {
        var callId = msg.I();
        var state = Delegates.entrySet().stream().filter(d -> d.getValue().CallId == callId).findFirst().orElseThrow();
        var delegate = state.getValue().Entry;
        var args = (Object[]) msg.R();
        var method = Reflect.getTheMethod(delegate);
        var pars = method.getParameters();
        var argLen = args == null ? 0 : args.length;
        for (var i = 0; i < argLen; i++) {
            var par = pars[i].getType();
            if (par == Object.class)
                continue;
            var arg = args[i];
            if (!(arg instanceof Object[] oa) || par == Object[].class)
                continue;
            args[i] = Conversions.fromObjectArray(par, oa);
        }
        Reflect.invoke(method, delegate, args);
    }

    public Object request(Method method, MethodCall call) throws InterruptedException {
        Object answer;
        if (Reflect.isAsync(method))
            answer = requestAsync(method, call);
        else
            answer = requestSync(method, call);
        return answer;
    }

    private Object requestSync(Method method, MethodCall msg) throws InterruptedException {
        createState(msg, true);
        _protocol.send(msg);
        var raw = waitSignal(msg);
        var res = extract(raw, method.getGenericReturnType());
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> continueLater(CompletableFuture<Object> task, Type type) {
        return task.thenApply(previous -> {
            var raw = previous;
            var res = extract(raw, type);
            return (T) res;
        });
    }

    private static Object extract(Object res, Type returnType) {
        if (res instanceof MethodResult mr) {
            return unpack(returnType, mr);
        }
        throw new UnsupportedOperationException(res + " ?!");
    }

    private Object requestAsync(Method method, MethodCall msg) {
        createState(msg, false);
        _protocol.send(msg);
        var taskType = Reflect.getTaskType(method.getGenericReturnType(), Object.class);
        var task = pinSignal(msg);
        var next = continueLater(task, taskType);
        return next;
    }

    private static Object unpack(Type returnType, MethodResult input) {
        var status = Arrays.stream(MethodStatus.values()).filter(m -> m.getValue() == input.S()).findFirst()
                .orElseThrow();
        switch (status) {
            case Ok:
                var raw = getCompatibleValue(returnType, input.R());
                return raw;
            default:
                throw new UnsupportedOperationException("[" + input.S() + "] " + input.R());
        }
    }

    private static Object getCompatibleValue(Type retType, Object retVal) {
        return Conversions.convert(retType, retVal);
    }

    public void listen() {
        var label = getClass().getSimpleName();
        _executor.createThread(this::listenAndWait, label + "|Listen");
    }

    public void listenAndWait() {
        _running = true;
        while (_running)
            try {
                if (_protocol instanceof IPullTransport put) {
                    var pulled = put.pull(MethodResult.class);
                    setSignal(pulled);
                } else if (_protocol instanceof IPushTransport pst) {
                    pst.onPush(this::setSignal, MethodResult.class);
                    break;
                }
            } catch (Exception ie) {
                _running = false;
            }
    }
}
