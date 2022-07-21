package jnetcall.java.server;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

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
        var type = _instance.getClass();
        for (var infType : type.getInterfaces())
            addServiceEndpoint(infType);
    }

    @Override
    public void addServiceEndpoint(Class<?> interfaceClass) {
        var name = interfaceClass.getSimpleName().toLowerCase();
        var subMap = new LinkedHashMap<String, BiFunction<Object, Object[], Object>>();
        _callMap.put(name, subMap);

        var methods = interfaceClass.getMethods();
        for (var method : methods) {
            var methodId = ClassTools.toMethodId(method);
            subMap.put(methodId, (obj, args) -> Reflect.invoke(method, obj, args));
        }
    }

    @Override
    public void close() throws Exception {
        _running = false;
        _executor.close();
        if (_instance instanceof AutoCloseable ac)
            ac.close();
        _callMap.clear();
        _protocol.close();
    }

    private static CompletableFuture<MethodResult> pack(Object res, MethodStatus status, short id) {
        var task = (res instanceof Future<?> future
                ? Tasks.wrap(future)
                : CompletableFuture.completedFuture(res))
                .thenApply(data -> {
                    var obj = new MethodResult(id, data, status.getValue());
                    return obj;
                });
        return task;
    }

    private static CompletableFuture<MethodResult> pack(Exception e, short id) {
        var cause = e instanceof RuntimeException re
                ? re.getCause()
                : e;
        cause = cause instanceof InvocationTargetException ti
                ? ti.getCause()
                : cause;
        var debug = Strings.getStackTrace(cause);
        return pack(debug, MethodStatus.MethodFailed, id);
    }

    private CompletableFuture<MethodResult> handle(MethodCall call) {
        var callId = call.I();
        var callIt = call.C().toLowerCase();
        if (!_callMap.containsKey(callIt)) {
            var debug = call.C();
            return pack(debug, MethodStatus.ClassNotFound, callId);
        }
        var subMap = _callMap.get(callIt);
        var methodIds = ClassTools.toMethodId(call);
        BiFunction<Object, Object[], Object> func;
        if ((func = subMap.getOrDefault(methodIds.getValue0(), null)) == null)
            func = subMap.getOrDefault(methodIds.getValue1(), null);
        if (func == null) {
            var debug = call.C() + "::" + call.M();
            return pack(debug, MethodStatus.MethodNotFound, callId);
        }
        try {
            var method = Reflect.getMethod(func);
            var rawArgs = rewriteArgsIfNeeded(call.A(), method.getParameters());
            var args = Conversions.convertFor(rawArgs, method);
            var res = func.apply(_instance, args);
            return pack(res, MethodStatus.Ok, callId);
        } catch (Exception e) {
            return pack(e, callId);
        }
    }

    private static Object wrapToDelegate(Type prm, DelegateWrap obj) {
        var loader = DelegateWrap.class.getClassLoader();
        var interf = (Class<?>) prm;
        var proxyHandle = Proxy.newProxyInstance(loader, new Class[]{interf}, obj);
        return proxyHandle;
    }

    private Object[] rewriteArgsIfNeeded(Object[] args, Parameter[] pars) {
        for (var i = 0; i < args.length; i++) {
            var prm = pars[i].getType();
            if (!Reflect.isDelegate(prm))
                continue;
            var delId = (short) args[i];
            args[i] = wrapToDelegate(prm, new DelegateWrap(this, delId, prm));
        }
        return args;
    }

    @SuppressWarnings("unchecked")
    public <T> T goDynInvoke(Class<T> type, short callId, Object[] args) {
        var status = MethodStatus.Continue.getValue();
        var delInvoke = new MethodResult(callId, args, status);
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
            var task = handle(msg);
            var res = Reflect.getVal(task);
            _protocol.send(res);
        });
    }

    public void serve() {
        var label = getClass().getSimpleName();
        _executor.createThread(this::serveAndWait, label + "|Serve");
    }

    public void serveAndWait() {
        _running = true;
        while (_running)
            try {
                if (_protocol instanceof IPullTransport put) {
                    var pulled = put.pull(MethodCall.class);
                    run(pulled);
                } else if (_protocol instanceof IPushTransport pst) {
                    pst.onPush(this::run, MethodCall.class);
                    break;
                }
            } catch (Exception e) {
                _running = false;
            }
    }
}
