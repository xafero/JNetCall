package jnetproto.java;

import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
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
        if (length != size && size != 0)
            throw new IllegalArgumentException(length + " ; " + size);
        return bytes;
    }

    @Override
    public boolean readBool() throws IOException {
        return readU8() == 1;
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
    public char readChar() throws IOException {
        return (char)readI16();
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
    public Object readArray() throws IOException {
        var item = DataType.values()[_stream.read()];
        var rank = _stream.read();
        var lengths = new int[rank];
        for (var i = 0; i < rank; i++)
            lengths[i] = readI32();
        var clazz = DataTypes.getClass(item);
        var array = Array.newInstance(clazz, lengths);
        var indices = new int[rank];
        for (var i = 0; i < rank; i++)
            for (var j = 0; j < lengths[i]; j++) {
                var obj = readObject(item);
                indices[i] = j;
                Array.set(array, indices[0], obj);
            }
        return array;
    }

    @Override
    public Object readObject() throws IOException {
        var kind = DataType.values()[_stream.read()];
        return readObject(kind);
    }

    private Object readObject(DataType kind) throws IOException {
        switch (kind)
        {
            case Bool: return readBool();
            case U8: return readU8();
            case I8: return readI8();
            case I16: return readI16();
            case I32: return readI32();
            case I64: return readI64();
            case F32: return readF32();
            case F64: return readF64();
            case F128: return readF128();
            case Char: return readChar();
            case UTF8: return readUtf8();
            case Duration: return readDuration();
            case Timestamp: return readTimestamp();
            case Guid: return readGuid();
            case Array: return readArray();
            default: throw new IllegalArgumentException(kind.toString());
        }
    }
    
    @Override
    public void close() throws Exception {
        _stream.close();
    }
}
