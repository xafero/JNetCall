package jnetcall.java.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class ServiceClient {

    public static <T> T createNative(Class<T> clazz, String exe) {
        var handler = new ClrInterceptor(exe);
        return create(clazz, handler);
    }

    public static <T> T createMain(Class<T> clazz, String exe) {
        var handler = new NetInterceptor(exe);
        return create(clazz, handler);
    }

    private static <T> T create(Class<T> clazz, InvocationHandler handler) {
        var loader = clazz.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(loader, new Class[]{clazz}, handler);
        return proxy;
    }
}
