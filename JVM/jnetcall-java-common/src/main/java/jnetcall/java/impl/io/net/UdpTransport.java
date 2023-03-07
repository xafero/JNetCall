package jnetcall.java.impl.io.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;

public final class UdpTransport implements ISendTransport, IPullTransport, AutoCloseable {

    private final IEncoding<byte[]> _encoding;
    private final DatagramChannel _receiver;
    private final DatagramChannel _sender;

    public UdpTransport(IEncoding<byte[]> encoding,
                         String hostIn, int portIn, String hostOut, int portOut) {
        this(encoding, NetworkTools.toEndPoint(hostIn, portIn),
                NetworkTools.toEndPoint(hostOut, portOut));
    }

    private UdpTransport(IEncoding<byte[]> encoding,
                          SocketAddress endPointIn, SocketAddress endPointOut) {
        _encoding = encoding;
        try {
            _receiver = DatagramChannel.open().bind(endPointIn);
            _sender = DatagramChannel.open();
            _sender.connect(endPointOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T pull(Class<T> clazz) {
        try {
            ByteBuffer buff = ByteBuffer.allocate(65535);
            _receiver.receive(buff);
            return _encoding.decode(buff.array(), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void send(T payload) {
        try {
            byte[] bytes = _encoding.encode(payload);
            ByteBuffer buff = ByteBuffer.wrap(bytes);
            _sender.write(buff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        _encoding.close();
        _sender.close();
        _receiver.close();
    }
}
