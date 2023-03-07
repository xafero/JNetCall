package jnetcall.java.tests.io;

import java.util.concurrent.atomic.AtomicInteger;

import org.javatuples.Pair;

import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.net.UdpTransport;

public final class UdpTransportTest extends TransportTest {

    private static AtomicInteger _offset = new AtomicInteger();

    private static int getNextOffset() {
        return _offset.incrementAndGet();
    }

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        int offset = getNextOffset();
        int portOne = 11001 + offset;
        int portTwo = 11051 + offset;
        UdpTransport left = new UdpTransport(
                Encoding,
                "localhost", portOne,
                "localhost", portTwo
        );
        UdpTransport right = new UdpTransport(
                Encoding,
                "localhost", portTwo,
                "localhost", portOne
        );
        return Pair.with(left, right);
    }
}
