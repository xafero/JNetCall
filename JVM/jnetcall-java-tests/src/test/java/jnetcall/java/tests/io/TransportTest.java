package jnetcall.java.tests.io;

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
import org.example.impl.CalculatorService;
import org.javatuples.Pair;

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
        var both = getBoth();
        var left = both.getValue0();
        var right = both.getValue1();
        var client = createClient(clazz, left);
        createServer(TestedService.class, right, false);
        return client;
    }

    private static <T extends AutoCloseable> T createClient(Class<T> clazz, ISendTransport transport) {
        var executor = new ThreadExecutor();
        var interceptor = new ClassProxy(transport, executor);
        interceptor.listen();
        return ClientHelper.create(clazz, interceptor);
    }

    private static <T> IHosting createServer(Class<T> clazz, ISendTransport transport, boolean blocking) {
        var executor = new ThreadExecutor();
        var instance = Reflect.createNew(clazz);
        var hosting = new ClassHosting(instance, transport, executor);
        hosting.registerAll();
        if (blocking)
            hosting.serveAndWait();
        else
            hosting.serve();
        return hosting;
    }
}
