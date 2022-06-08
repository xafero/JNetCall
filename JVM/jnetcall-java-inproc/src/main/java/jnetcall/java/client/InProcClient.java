package jnetcall.java.client;

public final class InProcClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var handler = new ClrInterceptor(exe);
        return ClientHelper.create(clazz, handler);
    }
}
