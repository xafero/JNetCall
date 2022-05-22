package jnetproto.java;

import jnetproto.java.compat.BitConverter;
import jnetproto.java.compat.Reflect;
import jnetproto.java.compat.Tuples;
import org.javatuples.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class BinaryReader implements IDataReader {
    private final Charset _enc;
    private final InputStream _stream;

    public BinaryReader(InputStream stream) {
        _enc = StandardCharsets.UTF_8;
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
        return readI8() == 1;
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
        var item = Reflect.toDataType(_stream.read());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map readMap() throws IOException {
        var keyKind = Reflect.toDataType(_stream.read());
        var valKind = Reflect.toDataType(_stream.read());
        var size = readI32();
        var map = new TreeMap();
        for (var i = 0; i < size; i++) {
            var key = readObject(keyKind);
            var val = readObject(valKind);
            map.put(key, val);
        }
        return map;
    }

    @Override
    public Tuple readTuple() throws IOException {
        var size = readI8();
        var args = new Object[size];
        for (var i = 0; i < size; i++)
        {
            var obj = readObject();
            args[i] = obj;
        }
        var method = Arrays.stream(Tuples.class.getMethods())
                .filter(m -> m.getName().equals("create") && m.getParameterCount() == args.length);
        return (Tuple)Reflect.invoke(method.findFirst().orElseThrow(), null, args);
    }

    @Override
    public Object[] readBag() throws IOException {
        var size = readI8();
        var args = new Object[size];
        for (var i = 0; i < size; i++)
        {
            var obj = readObject();
            args[i] = obj;
        }
        return args;
    }

    @Override
    public byte[] readBinary() throws IOException {
        var size = readI32();
        return readBytes(size);
    }

    @Override
    public Set readSet() throws IOException {
        return (Set)readIterable(new TreeSet());
    }

    @Override
    public List readList() throws IOException {
        return (List)readIterable(new ArrayList());
    }

    private Iterable readIterable(Iterable coll) throws IOException
    {
        var valKind = Reflect.toDataType(_stream.read());
        var size = readI32();
        var adder = Reflect.getMethod(coll, "add", Object.class);
        for (var i = 0; i < size; i++)
        {
            var val = readObject(valKind);
            Reflect.invoke(adder, coll, new Object[] { val });
        }
        return coll;
    }

    @Override
    public Object readObject() throws IOException {
        var kind = Reflect.toDataType(_stream.read());
        return readObject(kind);
    }

    private Object readObject(DataType kind) throws IOException {
        switch (kind)
        {
            case Bool: return readBool();
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
            case Map: return readMap();
            case Tuple: return readTuple();
            case Set: return readSet();
            case List: return readList();
            case Bag: return readBag();
            case Binary: return readBinary();
            default: throw new IllegalArgumentException(kind.toString());
        }
    }
    
    @Override
    public void close() throws Exception {
        if (_stream == null)
            return;
        _stream.close();
    }
}
