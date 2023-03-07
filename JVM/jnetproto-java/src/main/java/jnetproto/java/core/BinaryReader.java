package jnetproto.java.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Stream;

import org.javatuples.Tuple;

import jnetbase.java.compat.J8;
import jnetbase.java.meta.Reflect;
import jnetbase.java.sys.BitConverter;
import jnetproto.java.api.DataType;
import jnetproto.java.api.IDataReader;
import jnetproto.java.tools.Tuples;

public class BinaryReader implements IDataReader {
    private final Charset _enc;
    private final InputStream _stream;

    public BinaryReader(InputStream stream) {
        _enc = StandardCharsets.UTF_8;
        _stream = stream;
    }

    private byte[] readBytes(int size) throws IOException {
        byte[] bytes = new byte[size];
        int length = _stream.read(bytes);
        if (length != size && size != 0)
            throw new IllegalArgumentException("Got " + length + " B instead of " + size + "!");
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
        return new BigDecimal(readUtf8(false));
    }

    @Override
    public char readChar() throws IOException {
        return (char) readI16();
    }

    @Override
    public String readUtf8() throws IOException {
        return readUtf8(true);
    }

    private String readUtf8(boolean wide) throws IOException {
        short size = wide ? readI16() : readI8();
        return _enc.decode(ByteBuffer.wrap(readBytes(size))).toString();
    }

    @Override
    public Duration readDuration() throws IOException {
        double ms = readF64();
        long cast = (long) ms;
        return Duration.ofMillis(cast);
    }

    @Override
    public LocalDateTime readTimestamp() throws IOException {
        long millis = readI64();
        int nano = readI32() * 100;
        return LocalDateTime.ofEpochSecond(millis, nano, ZoneOffset.UTC);
    }

    @Override
    public UUID readGuid() throws IOException {
        return BitConverter.toGuid(readBytes(16));
    }

    @Override
    public Object readArray() throws IOException {
        DataType item = DataTypes.toDataType(_stream.read());
        int rank = _stream.read();
        int[] lengths = new int[rank];
        for (int i = 0; i < rank; i++)
            lengths[i] = readI32();
        Class<?> clazz = DataTypes.getClass(item);
        Object array = Array.newInstance(clazz, lengths);
        int[] indices = new int[rank];
        for (int i = 0; i < rank; i++)
            for (int j = 0; j < lengths[i]; j++) {
                Object obj = readObject(item);
                indices[i] = j;
                Array.set(array, indices[0], obj);
            }
        return array;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map readMap() throws IOException {
        DataType keyKind = DataTypes.toDataType(_stream.read());
        DataType valKind = DataTypes.toDataType(_stream.read());
        int size = readI32();
        TreeMap map = new TreeMap();
        for (int i = 0; i < size; i++) {
            Object key = readObject(keyKind);
            Object val = readObject(valKind);
            map.put(key, val);
        }
        return map;
    }

    @Override
    public Tuple readTuple() throws IOException {
        byte size = readI8();
        Object[] args = new Object[size];
        for (int i = 0; i < size; i++) {
            Object obj = readObject();
            args[i] = obj;
        }
        Stream<Method> method = Arrays.stream(Tuples.class.getMethods())
                .filter(m -> m.getName().equals("create") && m.getParameterCount() == args.length);
        return (Tuple) Reflect.invoke(J8.orElseThrow(method.findFirst()), null, args);
    }

    @Override
    public Object[] readBag() throws IOException {
        byte size = readI8();
        Object[] args = new Object[size];
        for (int i = 0; i < size; i++) {
            Object obj = readObject();
            args[i] = obj;
        }
        return args;
    }

    @Override
    public byte[] readBinary() throws IOException {
        int size = readI32();
        return readBytes(size);
    }

    @Override
    public Object readNull() {
        return null;
    }

    @Override
    public Set<?> readSet() throws IOException {
        return (Set<?>) readIterable(new TreeSet<Object>());
    }

    @Override
    public List<?> readList() throws IOException {
        return (List<?>) readIterable(new ArrayList<Object>());
    }

    private Iterable<?> readIterable(Iterable<?> coll) throws IOException {
        DataType valKind = DataTypes.toDataType(_stream.read());
        int size = readI32();
        Method adder = Reflect.getMethod(coll, "add", Object.class);
        for (int i = 0; i < size; i++) {
            Object val = readObject(valKind);
            Reflect.invoke(adder, coll, new Object[]{val});
        }
        return coll;
    }

    @Override
    public Object readObject() throws IOException {
        DataType kind = DataTypes.toDataType(_stream.read());
        return readObject(kind);
    }

    private Object readObject(DataType kind) throws IOException {
        switch (kind) {
            case Bool:
                return readBool();
            case I8:
                return readI8();
            case I16:
                return readI16();
            case I32:
                return readI32();
            case I64:
                return readI64();
            case F32:
                return readF32();
            case F64:
                return readF64();
            case F128:
                return readF128();
            case Char:
                return readChar();
            case UTF8:
                return readUtf8();
            case Duration:
                return readDuration();
            case Timestamp:
                return readTimestamp();
            case Guid:
                return readGuid();
            case Array:
                return readArray();
            case Map:
                return readMap();
            case Tuple:
                return readTuple();
            case Set:
                return readSet();
            case List:
                return readList();
            case Bag:
                return readBag();
            case Binary:
                return readBinary();
            case Null:
                return readNull();
            default:
                throw new IllegalArgumentException("readObject " + kind);
        }
    }

    @Override
    public void close() throws Exception {
        if (_stream == null)
            return;
        _stream.close();
    }
}
