package com.jnetcall.java;

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

    public void open() {
    }

    @Override
    public void close() {
        this.interfaces.clear();
    }
}