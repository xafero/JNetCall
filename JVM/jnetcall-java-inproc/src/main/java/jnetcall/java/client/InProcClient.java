package jnetcall.java.client;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.client.tools.ClientHelper;

public final class InProcClient {

    public static <T> T create(Class<T> clazz, String dll) {
        ThreadExecutor pool = new ThreadExecutor();
        ClrTransport protocol = new ClrTransport(dll, 15);
        ClassProxy handler = new ClassProxy(protocol, pool);
        handler.listen();
        return ClientHelper.create(clazz, handler);
    }
}
