package com.jnetcall.java;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jnetproto.java.ProtoConvert;
import jnetproto.java.ProtoSettings;
import jnetproto.java.compat.Strings;

public final class ServiceHost<T> implements AutoCloseable {
    private final Class<T> serviceClass;
    private final Map<String, Class<?>> interfaces;
	private final ProtoSettings config;

    public ServiceHost(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.interfaces = new HashMap<>();
        this.config = new ProtoSettings();
    }

    public <I> void addServiceEndpoint(Class<I> interfaceClass) {
        var name = interfaceClass.getSimpleName();
        this.interfaces.put(name, interfaceClass);
    }

    private T createInst() throws Exception {
        var clazz = this.serviceClass;
        var svc = clazz.getDeclaredConstructor().newInstance();
        return svc;
    }

    private void Write(ProtoConvert proto, Object res, MethodStatus status)
    		throws Exception {
        var obj = new MethodResult(res, status.getValue());
        proto.writeObject(obj);
        proto.flush();
    }

    private static ProtoConvert readSync(InputStream in, OutputStream out, ProtoSettings cfg)
            throws IOException {
        var convert = new ProtoConvert(in, out, cfg);
        final int marker = 0xEE;
        while (in.read() != marker);
        out.write(marker);
        out.flush();
        return convert;
    }

    public void open(InputStream input, OutputStream output) throws Exception {
        var inst = createInst();
        var methods = inst.getClass().getMethods();
        try (var proto = readSync(input, output, config)) {
            MethodCall call;
            while ((call = proto.readObject(MethodCall.class)) != null) {
                if (!interfaces.containsKey(call.C())) {
                    var debug = call.C();
                    Write(proto, debug, MethodStatus.ClassNotFound);
                    continue;
                }
                var callName = call.M();
                var method = Arrays.stream(methods)
                        .filter(m -> checkMethod(m, callName))
                        .findFirst().orElse(null);
                if (method == null) {
                    var debug = call.C() + "::" + call.M();
                    Write(proto, debug, MethodStatus.MethodNotFound);
                    continue;
                }
                try {
                    var types = method.getParameterTypes();
                    var args = Conversions.convert(types, call.A());
                    var res = method.invoke(inst, args);
                    Write(proto, res, MethodStatus.Ok);
                } catch (Throwable e) {
                    var cause = e instanceof InvocationTargetException
                            ? e.getCause() : e;
                    var debug = Strings.getStackTrace(cause);
                    Write(proto, debug, MethodStatus.MethodFailed);
                }
            }
        }
    }

    private boolean checkMethod(Method m, String callName) {
        return m.getName().equalsIgnoreCase(callName);
    }

    @Override
    public void close() {
        this.interfaces.clear();
    }
}