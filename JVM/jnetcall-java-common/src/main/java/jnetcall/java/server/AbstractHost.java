package jnetcall.java.server;

import jnetcall.java.api.MethodCall;
import jnetcall.java.api.MethodResult;
import jnetcall.java.api.MethodStatus;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;
import jnetbase.java.*;
import jnetproto.java.tools.Conversions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

abstract class AbstractHost<T> implements AutoCloseable {
    private final Class<T> serviceClass;
    private final Map<String, Class<?>> interfaces;
    protected final ProtoSettings config;

    protected AbstractHost(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.interfaces = new HashMap<>();
        this.config = new ProtoSettings();
    }

    protected T createInst() throws Exception {
        var clazz = this.serviceClass;
        var svc = clazz.getDeclaredConstructor().newInstance();
        return svc;
    }

    public <I> void addServiceEndpoint(Class<I> interfaceClass) {
        var name = interfaceClass.getSimpleName();
        this.interfaces.put(name, interfaceClass);
    }

    protected void handleCall(Object inst, Method[] methods, MethodCall call, ProtoConvert proto)
            throws Exception {
        var callId = call.I();
        if (!interfaces.containsKey(call.C())) {
            var debug = call.C();
            write(proto, debug, MethodStatus.ClassNotFound, callId);
            return;
        }
        var callName = call.M();
        var method = Arrays.stream(methods)
                .filter(m -> checkMethod(m, callName))
                .findFirst().orElse(null);
        if (method == null) {
            var debug = call.C() + "::" + call.M();
            write(proto, debug, MethodStatus.MethodNotFound, callId);
            return;
        }
        try {
            var args = Conversions.convertFor(call.A(), method);
            var res = method.invoke(inst, args);
            if (res instanceof Future<?> task) {
                // TODO Handle non-sync!
                res = getTaskResult(task);
            }
            write(proto, res, MethodStatus.Ok, callId);
        } catch (Throwable e) {
            var cause = e instanceof InvocationTargetException
                    ? e.getCause() : e;
            var debug = Strings.getStackTrace(cause);
            write(proto, debug, MethodStatus.MethodFailed, callId);
        }
    }

    private static Object getTaskResult(Future<?> task)
            throws ExecutionException, InterruptedException {
        var raw = task.get();
        return raw;
    }

    private boolean checkMethod(Method m, String callName) {
        var cName = callName.replace("_", "");
        return m.getName().equalsIgnoreCase(cName);
    }

    private static void write(ProtoConvert proto, Object res, MethodStatus status, short id)
            throws Exception {
        var obj = new MethodResult(id, res, status.getValue());
        proto.writeObject(obj);
        proto.flush();
    }

    @Override
    public void close() {
        this.interfaces.clear();
    }
}
