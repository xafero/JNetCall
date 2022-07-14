package jnetcall.java.client.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class ClientHelper {

    public static <T> T create(Class<T> clazz, InvocationHandler handler) {
        var loader = clazz.getClassLoader();
        var types = new Class[]{clazz};
        var proxy = Proxy.newProxyInstance(loader, types, handler);
        return (T) proxy;
    }
}
