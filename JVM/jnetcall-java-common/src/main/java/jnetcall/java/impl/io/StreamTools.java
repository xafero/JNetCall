package jnetcall.java.impl.io;

import jnetbase.java.sys.BitConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class StreamTools {

    private static byte[] tryRead(InputStream stream, int size, byte[] prefix)
            throws IOException {
        var skip = prefix != null ? prefix.length : 0;
        size += skip;
        var buffer = new byte[size];
        int got;
        if (prefix == null) {
            got = stream.read(buffer);
        } else {
            buffer = Arrays.copyOf(prefix, size);
            var tmp = stream.read(buffer, skip, size - skip);
            got = tmp + skip;
        }
        if (size != got) {
            throw new UnsupportedOperationException(size + " != " + got);
        }
        return buffer;
    }

    public static byte[] readWithSize(InputStream stream) throws IOException {
        var sizeBytes = tryRead(stream, 4, null);
        var size = BitConverter.toInt32(sizeBytes);
        var bytes = tryRead(stream, size, sizeBytes);
        return bytes;
    }
}
