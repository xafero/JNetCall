package jnetcall.java.client;

public final class StdIOClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var handler = new NetInterceptor(exe);
        return ClientHelper.create(clazz, handler);
    }
}
