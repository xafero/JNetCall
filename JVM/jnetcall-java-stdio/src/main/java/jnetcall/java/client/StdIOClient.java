package jnetcall.java.client;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.tools.ClientHelper;
import jnetcall.java.impl.enc.BinaryEncoding;
import jnetcall.java.impl.io.StreamTransport;

import java.io.InputStream;
import java.io.OutputStream;

public final class StdIOClient {

    public static <T> T create(Class<T> clazz, String exe) {
        var pool = new ThreadExecutor();
        var protocol = new ExeTransport(exe, StdIOClient::initDefault);
        var handler = new ClassProxy(protocol, pool);
        handler.listen();
        return ClientHelper.create(clazz, handler);
    }

    private static ISendTransport initDefault(InputStream stdIn, OutputStream stdOut) {
        var enc = new BinaryEncoding();
        return new StreamTransport(enc, stdIn, stdOut);
    }
}
