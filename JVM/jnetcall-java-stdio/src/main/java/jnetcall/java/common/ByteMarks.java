package jnetcall.java.common;

import jnetproto.java.tools.Tuples;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteMarks {

    public static Pair<InputStream, OutputStream> writeSync(InputStream stdOut, OutputStream stdIn)
            throws IOException {
        final int marker = 0xEE;
        // Send flag
        stdIn.write(marker);
        stdIn.flush();
        // Receive flag
        while (stdOut.read() != marker) ;
        // Ready!
        return Tuples.create(stdOut, stdIn);
    }
}
