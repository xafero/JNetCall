package jnetcall.java.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import org.javatuples.Pair;

import jnetbase.java.meta.Reflect;
import jnetbase.java.sys.Strings;
import jnetbase.java.threads.IExecutor;
import jnetbase.java.threads.Tasks;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.flow.MethodStatus;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.IPushTransport;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.util.ClassTools;
import jnetcall.java.server.api.IHosting;
import jnetcall.java.server.model.DelegateWrap;
import jnetproto.java.tools.Conversions;

public final class ClassHosting implements IHosting {

    private final IExecutor _executor;
    private final ISendTransport _protocol;
    private final Map<String, Map<String, BiFunction<Object, Object[], Object>>> _callMap;
    private final Object _instance;

    private boolean _running;

    public ClassHosting(Object instance, ISendTransport protocol, IExecutor executor) {
        _executor = executor;
        _protocol = protocol;
        _callMap = new LinkedHashMap<String, Map<String, BiFunction<Object, Object[], Object>>>();
        _instance = instance;
    }

    public void registerAll() {
        Class type = _instance.getClass();
        for (Class<?> infType : Reflect.getInterfaces(type))
            addServiceEndpoint(infType);
    }

    @Override
    public void addServiceEndpoint(Class<?> interfaceClass) {
        String name = interfaceClass.getSimpleName().toLowerCase();
        HashMap<String, BiFunction<Object, Object[], Object>> subMap = new LinkedHashMap<String, BiFunction<Object, Object[], Object>>();
        _callMap.put(name, subMap);

        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            String methodId = ClassTools.toMethodId(method);
            subMap.put(methodId, (obj, args) -> Reflect.invoke(method, obj, args));
        }
    }

    @Override
    public void close() throws Exception {
        _running = false;
        _executor.close();
        if (_instance instanceof AutoCloseable) {
        	AutoCloseable ac = (AutoCloseable)_instance;
            ac.close();
        }
        _callMap.clear();
        _protocol.close();
    }

    private static CompletableFuture<MethodResult> pack(Object res, MethodStatus status, short id) {
    	CompletableFuture<MethodResult> task = (res instanceof Future<?>
                ? Tasks.wrap((Future<?>)res)
                : CompletableFuture.completedFuture(res))
                .thenApply(data -> {
                    MethodResult obj = new MethodResult(id, data, status.getValue());
                    return obj;
                });
        return task;
    }

    private static CompletableFuture<MethodResult> pack(Exception e, short id) {
        Throwable cause = e instanceof RuntimeException
                ? ((RuntimeException)e).getCause()
                : e;
        cause = cause instanceof InvocationTargetException
                ? ((InvocationTargetException)cause).getCause()
                : cause;
        String debug = Strings.getStackTrace(cause);
        return pack(debug, MethodStatus.MethodFailed, id);
    }

    private CompletableFuture<MethodResult> handle(MethodCall call) {
        short callId = call.I();
        String callIt = call.C().toLowerCase();
        if (!_callMap.containsKey(callIt)) {
            String debug = call.C();
            return pack(debug, MethodStatus.ClassNotFound, callId);
        }
        Map<String, BiFunction<Object, Object[], Object>> subMap = _callMap.get(callIt);
        Pair<String, String> methodIds = ClassTools.toMethodId(call);
        BiFunction<Object, Object[], Object> func;
        if ((func = subMap.getOrDefault(methodIds.getValue0(), null)) == null)
            func = subMap.getOrDefault(methodIds.getValue1(), null);
        if (func == null) {
            String debug = call.C() + "::" + call.M();
            return pack(debug, MethodStatus.MethodNotFound, callId);
        }
        try {
            Method method = Reflect.getMethod(func);
            Object[] rawArgs = rewriteArgsIfNeeded(call.A(), method.getParameters());
            Object[] args = Conversions.convertFor(rawArgs, method);
            Object res = func.apply(_instance, args);
            return pack(res, MethodStatus.Ok, callId);
        } catch (Exception e) {
            return pack(e, callId);
        }
    }

    private static Object wrapToDelegate(Type prm, DelegateWrap obj) {
        ClassLoader loader = DelegateWrap.class.getClassLoader();
        Class<?> interf = (Class<?>) prm;
        Object proxyHandle = Proxy.newProxyInstance(loader, new Class[]{interf}, obj);
        return proxyHandle;
    }

    private Object[] rewriteArgsIfNeeded(Object[] args, Parameter[] pars) {
        for (int i = 0; i < args.length; i++) {
            Class<?> prm = pars[i].getType();
            if (!Reflect.isDelegate(prm))
                continue;
            short delId = (short) args[i];
            args[i] = wrapToDelegate(prm, new DelegateWrap(this, delId, prm));
        }
        return args;
    }

    @SuppressWarnings("unchecked")
    public <T> T goDynInvoke(Class<T> type, short callId, Object[] args) {
        short status = MethodStatus.Continue.getValue();
        MethodResult delInvoke = new MethodResult(callId, args, status);
        _protocol.send(delInvoke);
        if (type == boolean.class)
            return (T) (Object) true;
        if (type == Object.class || type == void.class)
            return (T) null;
        throw new UnsupportedOperationException(type.getName());
    }

    private void run(MethodCall msg) {
        CompletableFuture.runAsync(() ->
        {
            CompletableFuture<MethodResult> task = handle(msg);
            MethodResult res = Reflect.getVal(task);
            _protocol.send(res);
        });
    }

    public void serve() {
        String label = getClass().getSimpleName();
        _executor.createThread(this::serveAndWait, label + "|Serve");
    }

    public void serveAndWait() {
        _running = true;
        while (_running)
            try {
                if (_protocol instanceof IPullTransport) {
                	IPullTransport put = (IPullTransport)_protocol;
                    MethodCall pulled = put.pull(MethodCall.class);
                    run(pulled);
                } else if (_protocol instanceof IPushTransport) {
                	IPushTransport pst = (IPushTransport)_protocol;
                    pst.onPush(this::run, MethodCall.class);
                    break;
                }
            } catch (Exception e) {
                _running = false;
            }
    }
}
