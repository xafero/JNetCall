package jnetcall.java.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class ClientHelper {

    public static <T> T create(Class<T> clazz, InvocationHandler handler) {
        var loader = clazz.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(loader, new Class[]{clazz}, handler);
        return proxy;
    }
}
