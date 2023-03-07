package jnetcall.java.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.common.ByteMarks;
import jnetcall.java.impl.enc.BinaryEncoding;
import jnetcall.java.impl.io.StreamTransport;

public final class ServiceHosts {

    public static <T> ClassHosting create(Class<T> serviceClass) throws IOException {
        InputStream stdIn = System.in;
        PrintStream stdOut = System.out;
        ByteMarks.writeSync(stdIn, stdOut);
        ISendTransport protocol = initDefault(stdIn, stdOut);
        return create(serviceClass, protocol);
    }

    private static ISendTransport initDefault(InputStream stdIn, OutputStream stdOut) {
        BinaryEncoding enc = new BinaryEncoding();
        return new StreamTransport(enc, stdIn, stdOut);
    }

    private static <T> ClassHosting create(Class<T> serviceClass, ISendTransport protocol) {
        T instance = Reflect.createNew(serviceClass);
        ThreadExecutor pool = new ThreadExecutor();
        ClassHosting host = new ClassHosting(instance, protocol, pool);
        return host;
    }
}
