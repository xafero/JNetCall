package jnetcall.java.tests.io;

import org.javatuples.Pair;

import jnetbase.java.io.MemPipeStream;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.StreamTransport;

public final class MemTransportTest extends TransportTest {

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        MemPipeStream mem11001 = new MemPipeStream();
        MemPipeStream mem11002 = new MemPipeStream();
        StreamTransport left = new StreamTransport(
                Encoding,
                mem11001.asI(),
                mem11002.asO()
        );
        StreamTransport right = new StreamTransport(
                Encoding,
                mem11002.asI(),
                mem11001.asO()
        );
        return Pair.with(left, right);
    }
}
