package jnetcall.java.client;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.client.tools.ClientHelper;

public final class InProcClient {

    public static <T> T create(Class<T> clazz, String dll) {
        var pool = new ThreadExecutor();
        var protocol = new ClrTransport(dll);
        var handler = new ClassProxy(protocol, pool);
        handler.listen();
        return ClientHelper.create(clazz, handler);
    }
}
