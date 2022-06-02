package jnetproto.java.compat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ByteArrayStream extends ByteArrayInputStream {

    private final ByteArrayOutputStream _memory;

    public ByteArrayStream() {
        super(new byte[0]);
        _memory = new ByteArrayOutputStream();
    }

    public void write(byte[] result) throws IOException {
        _memory.write(result);
    }
}
