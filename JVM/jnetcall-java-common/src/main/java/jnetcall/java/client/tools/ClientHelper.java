package jnetcall.java.client.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class ClientHelper {

    public static <T> T create(Class<T> clazz, InvocationHandler handler) {
        ClassLoader loader = clazz.getClassLoader();
        Class[] types = new Class[]{clazz};
        Object proxy = Proxy.newProxyInstance(loader, types, handler);
        return (T) proxy;
    }
}
