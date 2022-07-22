package jnetcall.java.tests.io;

import jnetbase.java.io.MemPipeStream;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.StreamTransport;
import org.javatuples.Pair;

public final class MemTransportTest extends TransportTest {

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        var mem11001 = new MemPipeStream();
        var mem11002 = new MemPipeStream();
        var left = new StreamTransport(
                Encoding,
                mem11001.asI(),
                mem11002.asO()
        );
        var right = new StreamTransport(
                Encoding,
                mem11002.asI(),
                mem11001.asO()
        );
        return Pair.with(left, right);
    }
}
