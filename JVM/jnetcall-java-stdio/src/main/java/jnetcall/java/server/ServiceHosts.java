package jnetcall.java.server;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.common.ByteMarks;
import jnetcall.java.impl.enc.BinaryEncoding;
import jnetcall.java.impl.io.StreamTransport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ServiceHosts {

    public static <T> ClassHosting create(Class<T> serviceClass) throws IOException {
        var stdIn = System.in;
        var stdOut = System.out;
        ByteMarks.writeSync(stdIn, stdOut);
        var protocol = initDefault(stdIn, stdOut);
        return create(serviceClass, protocol);
    }

    private static ISendTransport initDefault(InputStream stdIn, OutputStream stdOut) {
        var enc = new BinaryEncoding();
        return new StreamTransport(enc, stdIn, stdOut);
    }

    private static <T> ClassHosting create(Class<T> serviceClass, ISendTransport protocol) {
        var instance = Reflect.createNew(serviceClass);
        var pool = new ThreadExecutor();
        var host = new ClassHosting(instance, protocol, pool);
        return host;
    }
}
