package jnetproto.java.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import jnetbase.java.meta.TypeToken;
import jnetproto.java.api.IDataReader;
import jnetproto.java.api.IDataWriter;
import jnetproto.java.core.BinaryReader;
import jnetproto.java.core.BinaryWriter;
import jnetproto.java.tools.Conversions;

public final class ProtoConvert implements AutoCloseable {

    private final Object _writeLock = new Object();
    private final Object _readLock = new Object();

    private final IDataReader _reader;
    private final IDataWriter _writer;
    private final ProtoSettings _cfg;

    public ProtoConvert(InputStream stdOutput, OutputStream stdInput, ProtoSettings cfg) {
        _reader = new BinaryReader(stdOutput);
        _writer = new BinaryWriter(stdInput);
        _cfg = cfg;
    }

    public void writeObject(Object obj) throws Exception {
        byte[] bytes = serializeObject(obj, _cfg);
        synchronized (_writeLock) {
            _writer.writeBinary(bytes);
        }
    }

    public <T> T readObject(Class<T> clazz) throws Exception {
        TypeToken<T> token = TypeToken.wrap(clazz);
        return readObject(token);
    }

    public <T> T readObject(TypeToken<T> type) throws Exception {
        byte[] bytes;
        synchronized (_readLock) {
            bytes = _reader.readBinary();
        }
        return deserializeObject(type, bytes, _cfg);
    }

    public void flush() throws IOException {
        synchronized (_writeLock) {
            _writer.flush();
        }
    }

    @Override
    public void close() throws Exception {
        _reader.close();
        _writer.close();
    }

    private static byte[] serializeObject(Object obj, ProtoSettings s)
            throws Exception {
        Object raw = Conversions.toObjectArray(obj);
        Object[] args = (Object[]) raw;
        return serializeObject(args, s);
    }

    private static <T> T deserializeObject(TypeToken<T> token, Object[] args, ProtoSettings s)
            throws Exception {
        Type type = token.toType();
        Object raw = Conversions.fromObjectArray(type, args);
        return (T) raw;
    }

    private static byte[] serializeObject(Object[] args, ProtoSettings s)
            throws Exception {
        try (ByteArrayOutputStream mem = new ByteArrayOutputStream();
             IDataWriter writer = new BinaryWriter(mem)) {
            writer.writeObject(args);
            return mem.toByteArray();
        }
    }

    private static <T> T deserializeObject(TypeToken<T> clazz, byte[] bytes, ProtoSettings s)
            throws Exception {
        try (ByteArrayInputStream mem = new ByteArrayInputStream(bytes);
             IDataReader reader = new BinaryReader(mem)) {
            Object[] args = (Object[]) reader.readObject();
            return deserializeObject(clazz, args, s);
        }
    }
}
