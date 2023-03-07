package jnetcall.java.tests.io;

import org.javatuples.Pair;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.ClassProxy;
import jnetcall.java.client.tools.ClientHelper;
import jnetcall.java.impl.enc.BinaryEncoding;
import jnetcall.java.server.ClassHosting;
import jnetcall.java.server.api.IHosting;
import jnetcall.java.tests.CallTest;

public abstract class TransportTest extends CallTest {

    protected abstract Pair<ISendTransport, ISendTransport> getBoth();

    protected final IEncoding<byte[]> Encoding = new BinaryEncoding();

    @Override
    protected String patch(String input) {
        return input.replace("E","E+")
                .replace("true","True")
                .replace("false","False");
    }

    @Override
    protected <T extends AutoCloseable> T create(Class<T> clazz) {
        Pair<ISendTransport, ISendTransport> both = getBoth();
        ISendTransport left = both.getValue0();
        ISendTransport right = both.getValue1();
        T client = createClient(clazz, left);
        createServer(TestedService.class, right, false);
        return client;
    }

    private static <T extends AutoCloseable> T createClient(Class<T> clazz, ISendTransport transport) {
        ThreadExecutor executor = new ThreadExecutor();
        ClassProxy interceptor = new ClassProxy(transport, executor);
        interceptor.listen();
        return ClientHelper.create(clazz, interceptor);
    }

    private static <T> IHosting createServer(Class<T> clazz, ISendTransport transport, boolean blocking) {
        ThreadExecutor executor = new ThreadExecutor();
        T instance = Reflect.createNew(clazz);
        ClassHosting hosting = new ClassHosting(instance, transport, executor);
        hosting.registerAll();
        if (blocking)
            hosting.serveAndWait();
        else
            hosting.serve();
        return hosting;
    }
}
