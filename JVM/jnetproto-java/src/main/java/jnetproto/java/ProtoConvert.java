package jnetproto.java;

import jnetproto.java.compat.Reflect;

import java.io.*;
import java.util.Arrays;

public final class ProtoConvert implements AutoCloseable {
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
        _writer.writeBinary(bytes);
    }

    public <T> T readObject(Class<T> clazz) throws Exception {
        var bytes = _reader.readBinary();
        return deserializeObject(clazz, bytes, _cfg);
    }

    public void flush() throws IOException {
        _writer.flush();
    }

    @Override
    public void close() throws Exception {
        _reader.close();
        _writer.close();
    }

    private static Object patch(Object obj) {
        // TODO: Complex sub structure?
        return obj;
    }

    private static byte[] serializeObject(Object obj, ProtoSettings s)
            throws Exception {
        var type = obj.getClass();
        var props = Reflect.getProperties(type);
        var args = new Object[props.size()];
        for (var i = 0; i < args.length; i++)
        {
            var getter = props.get(i).Get();
            args[i] = patch(Reflect.invoke(getter, obj, new Object[0]));
        }
        return serializeObject(args, s);
    }

    private static <T> T deserializeObject(Class<T> type, Object[] args, ProtoSettings s)
            throws Exception {
        var cTypes = Arrays.stream(args).map(a -> a.getClass()).toArray(Class[]::new);
        var creator = type.getConstructor(cTypes);
        if (creator == null) {
            throw new IllegalArgumentException("No constructor: " + type);
        }
        return creator.newInstance(args);
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
