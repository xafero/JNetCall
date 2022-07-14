package jnetcall.java.client;

import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.flow.MethodStatus;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;
import jnetproto.java.tools.Conversions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractInterceptor implements InvocationHandler, AutoCloseable {
    protected static final ProtoSettings settings = new ProtoSettings();

    protected String _exe;

    protected AbstractInterceptor(String exe) {
        try {
            _exe = exe;
            startBase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startBase() throws IOException {
        prepare();
        if (!(new File(_exe)).exists() && _exe.endsWith(".exe")) {
            _exe = _exe.substring(0, _exe.length() - 4);
        }
        if (!(new File(_exe)).exists()) {
            throw new FileNotFoundException("Missing: " + _exe);
        }
        start();
    }

    protected abstract void prepare();

    protected abstract void start() throws IOException;

    protected abstract void stop(int milliseconds) throws InterruptedException;

    protected abstract String getErrorDetails() throws IOException;

    @Override
    public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

    private static AtomicInteger _callId = new AtomicInteger(0);
    private static int getNextId() { return _callId.incrementAndGet(); }

    protected MethodCall pack(Method method, Object[] args) throws Exception {
        var contract = method.getDeclaringClass().getSimpleName();
        var action = method.getName();
        var safeArgs = args == null ? new Object[0] : args;
        var id = (short) getNextId();
        var call = new MethodCall(id, contract, action, safeArgs);
        if (call.C().equals("AutoCloseable") && call.M().equals("close")) {
            close();
            return null;
        }
        return call;
    }

    protected Object unpack(Method method, MethodResult input) {
        var status = Arrays.stream(MethodStatus.values())
                .filter(m -> m.getValue() == input.S())
                .findFirst().orElse(MethodStatus.Unknown);
        switch (status) {
            case Ok:
                var raw = getCompatibleValue(method.getGenericReturnType(), input.R());
                return raw;
            default:
                throw new UnsupportedOperationException("[" + input.S() + "] " + input.R());
        }
    }

    private static Object getCompatibleValue(Type retType, Object retVal) {
        if (retType instanceof ParameterizedType parmType && (parmType.getRawType().equals(CompletionStage.class)
                || parmType.getRawType().equals(CompletableFuture.class))) {
            var taskArgs = Arrays.stream(parmType.getActualTypeArguments()).findFirst().orElse(null);
            if (taskArgs == null) {
                return CompletableFuture.completedFuture(null);
            }
            var raw = Conversions.convert(taskArgs, retVal);
            return CompletableFuture.completedFuture(raw);
        }
        return Conversions.convert(retType, retVal);
    }

    protected Object invokeBase(Method method, Object[] args, ProtoConvert proto) throws Throwable {
        var call = pack(method, args);
        if (call == null)
            return null;
        write(proto, call);
        var answer = read(MethodResult.class, proto);
        return unpack(method, answer);
    }

    protected <T> T read(Class<T> clazz, ProtoConvert convert) {
        try {
            var obj = convert.readObject(clazz);
            return obj;
        } catch (Exception e) {
            String error = "";
            try {
                error = getErrorDetails();
            } catch (IOException ex) {
            }
            throw new UnsupportedOperationException(error, e);
        }
    }

    protected static void write(ProtoConvert convert, Object obj) throws Exception {
        convert.writeObject(obj);
        convert.flush();
    }

    @Override
    public void close() throws Exception {
        stop(250);
    }
}
