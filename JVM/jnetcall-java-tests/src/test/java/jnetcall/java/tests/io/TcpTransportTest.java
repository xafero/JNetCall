package jnetcall.java.tests.io;

import java.util.concurrent.atomic.AtomicInteger;

import org.javatuples.Pair;

import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.net.TcpTransport;

public final class TcpTransportTest extends TransportTest {

    private static AtomicInteger _offset = new AtomicInteger();

    private static int getNextOffset() {
        return _offset.incrementAndGet();
    }

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        int offset = getNextOffset();
        int portOne = 12001 + offset;
        int portTwo = 12051 + offset;
        TcpTransport left = new TcpTransport(
                Encoding,
                "localhost", portOne,
                "localhost", portTwo
        );
        TcpTransport right = new TcpTransport(
                Encoding,
                "localhost", portTwo,
                "localhost", portOne
        );
        return Pair.with(left, right);
    }
}
