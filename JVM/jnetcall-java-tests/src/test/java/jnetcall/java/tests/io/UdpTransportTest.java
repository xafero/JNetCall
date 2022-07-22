package jnetcall.java.tests.io;

import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.net.UdpTransport;
import org.javatuples.Pair;

import java.util.concurrent.atomic.AtomicInteger;

public final class UdpTransportTest extends TransportTest {

    private static AtomicInteger _offset = new AtomicInteger();

    private static int getNextOffset() {
        return _offset.incrementAndGet();
    }

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        var offset = getNextOffset();
        var portOne = 11001 + offset;
        var portTwo = 11051 + offset;
        var left = new UdpTransport(
                Encoding,
                "localhost", portOne,
                "localhost", portTwo
        );
        var right = new UdpTransport(
                Encoding,
                "localhost", portTwo,
                "localhost", portOne
        );
        return Pair.with(left, right);
    }
}
