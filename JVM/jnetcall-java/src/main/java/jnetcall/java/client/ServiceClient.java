package jnetcall.java.client;

import java.lang.reflect.Proxy;

public final class ServiceClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var handler = new NetInterceptor(exe);
        var loader = clazz.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(loader, new Class[]{clazz}, handler);
        return proxy;
    }
}
