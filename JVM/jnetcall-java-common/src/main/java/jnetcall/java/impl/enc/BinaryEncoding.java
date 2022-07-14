package jnetcall.java.impl.enc;

import jnetcall.java.api.enc.IByteEncoding;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BinaryEncoding implements IByteEncoding {

    private final ProtoSettings _config;

    public BinaryEncoding() {
        _config = new ProtoSettings();
    }

    @Override
    public <T> byte[] encode(T data) throws Exception {
        try (var output = new ByteArrayOutputStream();
             var proto = new ProtoConvert(null, output, _config)) {
            proto.writeObject(data);
            return output.toByteArray();
        }
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz) throws Exception {
        try (var input = new ByteArrayInputStream(data);
             var proto = new ProtoConvert(input, null, _config)) {
            var res = proto.readObject(clazz);
            return res;
        }
    }

    @Override
    public void close() throws Exception {
    }
}
