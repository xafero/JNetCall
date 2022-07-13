package jnetproto.java.beans;

import jnetbase.java.meta.Reflect;
import jnetproto.java.api.IDataReader;
import jnetproto.java.api.IDataWriter;
import jnetproto.java.core.BinaryReader;
import jnetproto.java.core.BinaryWriter;
import jnetproto.java.core.DataTypes;
import jnetproto.java.tools.Conversions;

import java.io.*;
import java.util.Arrays;

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
        var bytes = serializeObject(obj, _cfg);
        synchronized (_writeLock) {
            _writer.writeBinary(bytes);
        }
    }

    public <T> T readObject(Class<T> clazz) throws Exception {
        byte[] bytes;
        synchronized (_readLock) {
            bytes = _reader.readBinary();
        }
        return deserializeObject(clazz, bytes, _cfg);
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
        var raw = Conversions.toObjectArray(obj);
        var args = (Object[]) raw;
        return serializeObject(args, s);
    }

    private static <T> T deserializeObject(Class<T> type, Object[] args, ProtoSettings s)
            throws Exception {
        var raw = Conversions.fromObjectArray(type, args);
        return (T) raw;
    }

    private static byte[] serializeObject(Object[] args, ProtoSettings s)
            throws Exception {
        try (var mem = new ByteArrayOutputStream();
             IDataWriter writer = new BinaryWriter(mem)) {
            writer.writeObject(args);
            return mem.toByteArray();
        }
    }

    private static <T> T deserializeObject(Class<T> clazz, byte[] bytes, ProtoSettings s)
            throws Exception {
        try (var mem = new ByteArrayInputStream(bytes);
             IDataReader reader = new BinaryReader(mem)) {
            var args = (Object[]) reader.readObject();
            return deserializeObject(clazz, args, s);
        }
    }
}
