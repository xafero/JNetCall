package jnetcall.java.client;

import java.io.InputStream;
import java.io.OutputStream;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.tools.ClientHelper;
import jnetcall.java.impl.enc.BinaryEncoding;
import jnetcall.java.impl.io.StreamTransport;

public final class StdIOClient {

    public static <T> T create(Class<T> clazz, String exe) {
        ThreadExecutor pool = new ThreadExecutor();
        ExeTransport protocol = new ExeTransport(exe, StdIOClient::initDefault);
        ClassProxy handler = new ClassProxy(protocol, pool);
        handler.listen();
        return ClientHelper.create(clazz, handler);
    }

    private static ISendTransport initDefault(InputStream stdIn, OutputStream stdOut) {
        BinaryEncoding enc = new BinaryEncoding();
        return new StreamTransport(enc, stdIn, stdOut);
    }
}
