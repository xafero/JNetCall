package jnetbase.java.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ByteArrayStream extends ByteArrayInputStream {

    private ByteArrayOutputStream _memory;

    public ByteArrayStream() {
        super(new byte[0]);
        renew();
    }

    private void renew() {
        _memory = new ByteArrayOutputStream();
    }

    public void write(byte[] result) throws IOException {
        _memory.write(result);
    }

    @Override
    public synchronized void reset() {
        super.reset();

        var array = _memory.toByteArray();
        renew();

        buf = array;
        pos = 0;
        count = buf.length;
    }
}
