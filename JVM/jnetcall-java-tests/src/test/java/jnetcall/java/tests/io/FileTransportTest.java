package jnetcall.java.tests.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import org.javatuples.Pair;

import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.impl.io.disk.FolderTransport;

public final class FileTransportTest extends TransportTest {

    private final String Folder = "FileTmp";

    private static AtomicInteger _offset = new AtomicInteger();

    private static int getNextOffset() {
        return _offset.incrementAndGet();
    }

    @Override
    protected Pair<ISendTransport, ISendTransport> getBoth() {
        int offset = getNextOffset();
        Path first = Paths.get(Folder, 13001 + offset + "");
        Path second = Paths.get(Folder, 13051 + offset + "");
        ThreadExecutor exe = new ThreadExecutor();
        FolderTransport left = new FolderTransport(
                Encoding,
                first,
                second,
                exe
        );
        FolderTransport right = new FolderTransport(
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
