package jnetcall.java.impl.io;

import jnetbase.java.io.IRewindable;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;

import java.io.InputStream;
import java.io.OutputStream;

public final class StreamTransport implements ISendTransport, IPullTransport, AutoCloseable {

    private final IEncoding<byte[]> _encoding;
    private final InputStream _streamIn;
    private final OutputStream _streamOut;

    public StreamTransport(IEncoding<byte[]> encoding,
                            InputStream streamIn, OutputStream streamOut) {
        _encoding = encoding;
        _streamIn = streamIn;
        _streamOut = streamOut;
    }

    @Override
    public <T> T pull(Class<T> clazz) {
        try {
            var bytes = StreamTools.readWithSize(_streamIn);
            return _encoding.decode(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void send(T payload) {
        try {
            var bytes = _encoding.encode(payload);
            _streamOut.write(bytes, 0, bytes.length);
            _streamOut.flush();
            if (_streamOut instanceof IRewindable r)
                r.rewind(bytes.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        _streamOut.close();
        _streamIn.close();
        _encoding.close();
    }
}
