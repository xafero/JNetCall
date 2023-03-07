package jnetcall.java.client;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jnetbase.java.compat.J8;
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
        MethodCall call = pack(method, args);
        if (call == null)
            return null;
        Object answer = request(method, call);
        return answer;
    }

    private static AtomicInteger _callId = new AtomicInteger();

    private static int getNextId() {
        return _callId.incrementAndGet();
    }

    private MethodCall pack(Method method, Object[] rawArgs) throws Exception {
        short id = (short) getNextId();
        String source = method.getDeclaringClass().getSimpleName();
        Object[] args = rewriteArgsIfNeeded(rawArgs);
        MethodCall call = new MethodCall(id, source, method.getName(), args);
        if (call.C().equals("AutoCloseable") && call.M().equals("close")) {
            close();
            return null;
        }
        return call;
    }

    private static final Map<String, DelegateRef> Delegates = new HashMap<String, DelegateRef>();

    private static short wrapFromDelegate(Object del) {
        String delId = ClassTools.toDelegateId(del);
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
        Object[] args = new Object[raw.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = raw[i];
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
        for (Entry<Short, CallState> signal : _signals.entrySet())
        	disposeSignal(signal);
        _signals.clear();
    }

    private static void disposeSignal(Map.Entry<Short, CallState> signal) {
        CallState state = signal.getValue();
        state.Result = new InterruptedException("Dispose");
        state.set();
    }

    private CallState createState(ICall call, boolean sync) {
        short callId = call.I();
        CallState state = new CallState();
        if (sync)
            state.SyncWait = new ManualResetEvent(false);
        else
            state.AsyncWait = new ManualResetEvent(false);
        _signals.put(callId, state);
        return state;
    }

    private Object waitSignal(ICall call) throws InterruptedException {
        short id = call.I();
        CallState state = _signals.get(id);
        state.SyncWait.waitOne();
        Object res = state.Result;
        return res;
    }

    private CompletableFuture<Object> pinSignal(ICall call) {
        short id = call.I();
        Callable<Object> fut = () -> {
            CallState state = _signals.get(id);
            state.AsyncWait.waitOne();
            Object res = state.Result;
            return res;
        };
        CompletableFuture<Object> task = Tasks.wrap(fut);
        return task;
    }

    private void setSignal(ICall call) {
        if (call instanceof MethodResult && ((MethodResult)call).S() == MethodStatus.Continue.getValue()) {
            setDelegate((MethodResult)call);
            return;
        }
        short callId = call.I();
        CallState state = _signals.get(callId);
        state.Result = call;
        state.set();
    }

    private static void setDelegate(MethodResult msg) {
        short callId = msg.I();
        Entry<String, DelegateRef> state = J8.orElseThrow(Delegates.entrySet().stream().filter(d -> d.getValue().CallId == callId).findFirst());
        Object delegate = state.getValue().Entry;
        Object[] args = (Object[]) msg.R();
        Method method = Reflect.getTheMethod(delegate);
        Parameter[] pars = method.getParameters();
        int argLen = args == null ? 0 : args.length;
        for (int i = 0; i < argLen; i++) {
            Class par = pars[i].getType();
            if (par == Object.class)
                continue;
            Object arg = args[i];
            if (!(arg instanceof Object[]) || par == Object[].class)
                continue;
            Object[] oa = (Object[])arg;
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
        Object raw = waitSignal(msg);
        Object res = extract(raw, method.getGenericReturnType());
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> continueLater(CompletableFuture<Object> task, Type type) {
        return task.thenApply(previous -> {
            Object raw = previous;
            Object res = extract(raw, type);
            return (T) res;
        });
    }

    private static Object extract(Object res, Type returnType) {
        if (res instanceof MethodResult) {
        	MethodResult mr = (MethodResult)res;
            return unpack(returnType, mr);
        }
        throw new UnsupportedOperationException(res + " ?!");
    }

    private Object requestAsync(Method method, MethodCall msg) {
        createState(msg, false);
        _protocol.send(msg);
        Type taskType = Reflect.getTaskType(method.getGenericReturnType(), Object.class);
        CompletableFuture<Object> task = pinSignal(msg);
        CompletableFuture<Object> next = continueLater(task, taskType);
        return next;
    }

    private static Object unpack(Type returnType, MethodResult input) {
        MethodStatus status = J8.orElseThrow(Arrays.stream(MethodStatus.values()).filter(m -> m.getValue() == input.S()).findFirst());
        switch (status) {
            case Ok:
                Object raw = getCompatibleValue(returnType, input.R());
                return raw;
            default:
                throw new UnsupportedOperationException("[" + input.S() + "] " + input.R());
        }
    }

    private static Object getCompatibleValue(Type retType, Object retVal) {
        return Conversions.convert(retType, retVal);
    }

    public void listen() {
        String label = getClass().getSimpleName();
        _executor.createThread(this::listenAndWait, label + "|Listen");
    }

    public void listenAndWait() {
        _running = true;
        while (_running)
            try {
                if (_protocol instanceof IPullTransport) {
                	IPullTransport put = (IPullTransport)_protocol;
                    MethodResult pulled = put.pull(MethodResult.class);
                    setSignal(pulled);
                } else if (_protocol instanceof IPushTransport) {
                	IPushTransport pst = (IPushTransport)_protocol;
                    pst.onPush(this::setSignal, MethodResult.class);
                    break;
                }
            } catch (Exception ie) {
                _running = false;
            }
    }
}
