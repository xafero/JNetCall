package jnetcall.java.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jnetbase.java.sys.BitConverter;

public final class StreamTools {

    private static byte[] tryRead(InputStream stream, int size, byte[] prefix)
            throws IOException {
        int skip = prefix != null ? prefix.length : 0;
        size += skip;
        byte[] buffer = new byte[size];
        int got;
        if (prefix == null) {
            got = stream.read(buffer);
        } else {
            buffer = Arrays.copyOf(prefix, size);
            int tmp = stream.read(buffer, skip, size - skip);
            got = tmp + skip;
        }
        if (size != got) {
            throw new UnsupportedOperationException(size + " != " + got);
        }
        return buffer;
    }

    public static byte[] readWithSize(InputStream stream) throws IOException {
        byte[] sizeBytes = tryRead(stream, 4, null);
        int size = BitConverter.toInt32(sizeBytes);
        byte[] bytes = tryRead(stream, size, sizeBytes);
        return bytes;
    }
}
