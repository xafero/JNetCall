package jnetcall.java.server;

import jnetcall.java.api.flow.MethodCall;
import jnetproto.java.beans.ProtoConvert;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public final class ServiceLot<T> extends AbstractHost<T> implements AutoCloseable {
    private T _instance;
    private Method[] _methods;

    public ServiceLot(Class<T> serviceClass) {
        super(serviceClass);
    }

    public void build() {
        _instance = tryCreateInst();
        _methods = _instance.getClass().getMethods();
        ServiceLots.register(this);
    }

    boolean tryCall(byte[] in, OutputStream output) throws Exception {
        try (var input = new ByteArrayInputStream(in);
             var proto = new ProtoConvert(input, output, config)) {
            var call = proto.readObject(MethodCall.class);
            handleCall(_instance, _methods, call, proto);
            output.flush();
            return true;
        }
    }
}