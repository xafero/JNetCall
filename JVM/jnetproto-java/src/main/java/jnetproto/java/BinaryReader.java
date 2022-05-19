package jnetproto.java;

import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class BinaryReader implements IDataReader {
    private final Charset _enc;
    private final InputStream _stream;

    public BinaryReader(InputStream stream) {
        _enc = Charset.forName("UTF8");
        _stream = stream;
    }

    private byte[] readBytes(int size) throws IOException {
        var bytes = new byte[size];
        var length = _stream.read(bytes);
        if (length != size)
            throw new IllegalArgumentException(length + " ; " + size);
        return bytes;
    }

    @Override
    public byte readU8() throws IOException {
        return readBytes(1)[0];
    }

    @Override
    public byte readI8() throws IOException {
        return readBytes(1)[0];
    }

    @Override
    public short readI16() throws IOException {
        return BitConverter.toInt16(readBytes(2));
    }

    @Override
    public int readI32() throws IOException {
        return BitConverter.toInt32(readBytes(4));
    }

    @Override
    public long readI64() throws IOException {
        return BitConverter.toInt64(readBytes(8));
    }

    @Override
    public float readF32() throws IOException {
        return BitConverter.toSingle(readBytes(4));
    }

    @Override
    public double readF64() throws IOException {
        return BitConverter.toDouble(readBytes(8));
    }

    @Override
    public BigDecimal readF128() throws IOException {
        return new BigDecimal(readUtf8());
    }

    @Override
    public String readUtf8() throws IOException {
        return _enc.decode(ByteBuffer.wrap(readBytes(_stream.read()))).toString();
    }

    @Override
    public Duration readDuration() throws IOException {
        var ms = readF64();
        var cast = (long) ms;
        return Duration.ofMillis(cast);
    }

    @Override
    public LocalDateTime readTimestamp() throws IOException {
        var millis = readI64();
        var nano = readI32() * 100;
        return LocalDateTime.ofEpochSecond(millis, nano, ZoneOffset.UTC);
    }

    @Override
    public UUID readGuid() throws IOException {
        return BitConverter.toGuid(readBytes(16));
    }

    @Override
    public Object readObject() throws IOException {
        var kind = DataType.values()[_stream.read()];
        switch (kind)
        {
            case U8: return readU8();
            case I8: return readI8();
            case I16: return readI16();
            case I32: return readI32();
            case I64: return readI64();
            case F32: return readF32();
            case F64: return readF64();
            case F128: return readF128();
            case UTF8: return readUtf8();
            case Duration: return readDuration();
            case Timestamp: return readTimestamp();
            case Guid: return readGuid();
            default: throw new IllegalArgumentException(kind.toString());
        }
    }
    
    @Override
    public void close() throws Exception {
        _stream.close();
    }
}
