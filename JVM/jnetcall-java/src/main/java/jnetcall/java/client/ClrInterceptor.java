package jnetcall.java.client;

import jnetcall.java.api.MethodResult;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.compat.ByteArrayStream;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public final class ClrInterceptor extends AbstractInterceptor {

    public ClrInterceptor(String exe) {
        super(exe);
    }

    private static final Object Sync = new Object();
    private static AutoCloseable _vm;

    private static AutoCloseable setupClr(String exe) {
        synchronized (Sync) {
            if (_vm != null)
                return _vm;

            throw new UnsupportedOperationException("TODO!");
        }
    }

    @Override
    protected void prepare() {
        synchronized (Sync) {
            _vm = setupClr(_exe);
        }
    }

    @Override
    protected void start() {
        initBoot();
    }

    private static void initBoot() {
        throw new UnsupportedOperationException("TODO!");
    }

    private static byte[] sendAndGetArray(byte[] input) {
        throw new UnsupportedOperationException("TODO!");
    }

    @Override
    protected void stop(int milliseconds) {
        var domain = Runtime.getRuntime();
        domain.addShutdownHook(new Thread(() -> {
            try {
                _vm.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (var input = new ByteArrayStream();
             var output = new ByteArrayOutputStream();
             var proto = new ProtoConvert(input, output, settings)) {
            var call = pack(method, args);
            if (call == null)
                return null;
            write(proto, call);
            var array = output.toByteArray();
            var result = sendAndGetArray(array);
            input.write(result);
            input.reset();
            var answer = read(MethodResult.class, proto);
            return unpack(method, answer);
        }
    }

    @Override
    protected String getErrorDetails() {
        return "";
    }
}
