package com.jnetcall.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ServiceHost<T> implements AutoCloseable {
    private final Class<T> serviceClass;
    private final Map<String, Class> interfaces;

    public ServiceHost(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.interfaces = new HashMap<>();
    }

    public <I> void addServiceEndpoint(Class<I> interfaceClass) {
        var name = interfaceClass.getSimpleName();
        this.interfaces.put(name, interfaceClass);
    }

    private Gson createGson() {
        var builder = new GsonBuilder();
        var gson = builder.create();
        return gson;
    }

    private T createInst() throws Exception {
        var clazz = this.serviceClass;
        var svc = clazz.getDeclaredConstructor().newInstance();
        return svc;
    }

    private void Write(PrintWriter bw, Gson gson, Object res, MethodStatus status) {
        var obj = new MethodResult(res, status.getValue());
        var text = gson.toJson(obj);
        bw.println(text);
        bw.flush();
    }

    public void open(InputStream input, OutputStream output) throws Exception {
        var inst = createInst();
        var methods = inst.getClass().getMethods();
        var gson = createGson();
        try (var ir = new InputStreamReader(input);
             var or = new OutputStreamWriter(output);
             var br = new BufferedReader(ir);
             var bw = new PrintWriter(or)) {
            String json;
            while ((json = br.readLine()) != null) {
                var call = gson.fromJson(json, MethodCall.class);
                if (!interfaces.containsKey(call.C)) {
                    Write(bw, gson, call.C, MethodStatus.ClassNotFound);
                    continue;
                }
                var method = Arrays.stream(methods)
                        .filter(m -> m.getName().equalsIgnoreCase(call.M))
                        .findFirst().get();
                if (method == null) {
                    Write(bw, gson, call.M, MethodStatus.MethodNotFound);
                    continue;
                }
                try {
                    var args = call.A;
                    var res = method.invoke(inst, args);
                    Write(bw, gson, res, MethodStatus.Ok);
                } catch (Exception e) {
                    Write(bw, gson, e.getMessage(), MethodStatus.MethodFailed);
                }
            }
        }
    }

    @Override
    public void close() {
        this.interfaces.clear();
    }
}