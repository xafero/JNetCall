package jnetcall.java.client;

import jnetcall.java.client.tools.ClientHelper;

public final class StdIOClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var handler = new NetInterceptor(exe);
        return ClientHelper.create(clazz, handler);
    }
}
