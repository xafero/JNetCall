package com.jnetcall.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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

    private void Write(PrintWriter bw, ProtoConvert proto, Object res, MethodStatus status) 
    		throws Exception {
        var obj = new MethodResult(res, status.getValue());
        proto.writeObject(obj);
        proto.flush();
    }

    public void open(InputStream input, OutputStream output) throws Exception {
        var inst = createInst();
        var methods = inst.getClass().getMethods();
        try (var proto = new ProtoConvert(input, output, config);
        	 var ir = new InputStreamReader(input);
             var or = new OutputStreamWriter(output);
             var bw = new PrintWriter(or)) {
            MethodCall call;
            while ((call = proto.readObject(MethodCall.class)) != null) {
                if (!interfaces.containsKey(call.C)) {
                    var debug = call.C;
                    Write(bw, proto, debug, MethodStatus.ClassNotFound);
                    continue;
                }
                var callName = call.M;
                var method = Arrays.stream(methods)
                        .filter(m -> m.getName().equalsIgnoreCase(callName))
                        .findFirst().orElse(null);
                if (method == null) {
                    var debug = call.C + "::" + call.M;
                    Write(bw, proto, debug, MethodStatus.MethodNotFound);
                    continue;
                }
                try {
                    var types = method.getParameterTypes();
                    var args = Conversions.convert(types, call.A, call.H);
                    var res = method.invoke(inst, args);
                    Write(bw, proto, res, MethodStatus.Ok);
                } catch (Throwable e) {
                    var cause = e instanceof InvocationTargetException
                            ? e.getCause() : e;
                    var debug = Strings.getStackTrace(cause);
                    Write(bw, proto, debug, MethodStatus.MethodFailed);
                }
            }
        }
    }

    @Override
    public void close() {
        this.interfaces.clear();
    }
}