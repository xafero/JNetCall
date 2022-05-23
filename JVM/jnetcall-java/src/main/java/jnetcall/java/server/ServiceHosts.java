package jnetcall.java.server;

public final class ServiceHosts {

    public static <T> ServiceHost<T> create(Class<T> serviceClass) {
        var instance = new ServiceHost<>(serviceClass);
        return instance;
    }
}