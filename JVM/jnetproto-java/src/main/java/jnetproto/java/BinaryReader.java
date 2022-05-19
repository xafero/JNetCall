package jnetproto.java;

import java.io.InputStream;
import java.math.BigDecimal;

public class BinaryReader implements IDataReader {
    private final InputStream _stream;

    public BinaryReader(InputStream stream) {
        _stream = stream;
    }

    @Override
    public int readI32() {
        return 0;
    }

    @Override
    public long readI64() {
        return 0;
    }

    @Override
    public float readF32() {
        return 0;
    }

    @Override
    public double readF64() {
        return 0;
    }

    @Override
    public BigDecimal readF128() {
        return null;
    }

    /*
                var millis = 1;
            LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);

     */

    @Override
    public void close() throws Exception {
        _stream.close();
    }
}
