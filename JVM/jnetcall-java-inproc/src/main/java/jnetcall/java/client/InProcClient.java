package jnetcall.java.client;

import jnetcall.java.client.tools.ClientHelper;

public final class InProcClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var handler = new ClrInterceptor(exe);
        return ClientHelper.create(clazz, handler);
    }
}
