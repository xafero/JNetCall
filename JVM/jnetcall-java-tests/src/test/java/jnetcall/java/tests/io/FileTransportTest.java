package jnetcall.java.tests.io;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.disk.FolderTransport;
import org.javatuples.Pair;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public final class FileTransportTest extends TransportTest {

    private final String Folder = "FileTmp";

    private static AtomicInteger _offset = new AtomicInteger();

    private static int getNextOffset() {
        return _offset.incrementAndGet();
    }

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        var offset = getNextOffset();
        var first = Path.of(Folder, 13001 + offset + "");
        var second = Path.of(Folder, 13051 + offset + "");
        var exe = new ThreadExecutor();
        var left = new FolderTransport(
                Encoding,
                first,
                second,
                exe
        );
        var right = new FolderTransport(
                Encoding,
                second,
                first,
                exe
        );
        return Pair.with(left, right);
    }

    @Override
    protected int getMaxListWait() { return 20; }
}
