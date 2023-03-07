package jnetcall.java.impl.enc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jnetbase.java.meta.TypeToken;
import jnetcall.java.api.enc.IByteEncoding;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;

public final class BinaryEncoding implements IByteEncoding {

    private final ProtoSettings _config;

    public BinaryEncoding() {
        _config = new ProtoSettings();
    }

    @Override
    public <T> byte[] encode(T data) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
        		ProtoConvert proto = new ProtoConvert(null, output, _config)) {
            proto.writeObject(data);
            return output.toByteArray();
        }
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream input = new ByteArrayInputStream(data);
        		ProtoConvert proto = new ProtoConvert(input, null, _config)) {
            T res = proto.readObject(clazz);
            return res;
        }
    }

    @Override
    public <T> T decode(byte[] data, TypeToken<T> token) throws Exception {
        try (ByteArrayInputStream input = new ByteArrayInputStream(data);
        		ProtoConvert proto = new ProtoConvert(input, null, _config)) {
            T res = proto.readObject(token);
            return res;
        }
    }

    @Override
    public void close() throws Exception {
    }
}
