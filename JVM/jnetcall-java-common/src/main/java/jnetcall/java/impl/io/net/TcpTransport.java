package jnetcall.java.impl.io.net;

import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class TcpTransport implements ISendTransport, IPullTransport, AutoCloseable {

    private final IEncoding<byte[]> _encoding;
    private final ServerSocketChannel _receiver;
    private final SocketChannel _sender;
    private final SocketAddress _endPointOut;
    private final int _wait;

    public TcpTransport(IEncoding<byte[]> encoding,
                         String hostIn, int portIn, String hostOut, int portOut) {
        this(encoding, NetworkTools.toEndPoint(hostIn, portIn),
                NetworkTools.toEndPoint(hostOut, portOut));
    }

    private TcpTransport(IEncoding<byte[]> encoding,
                          SocketAddress endPointIn, SocketAddress endPointOut) {
        _encoding = encoding;
        try {
            _receiver = ServerSocketChannel.open().bind(endPointIn);
            _sender = SocketChannel.open();
            _endPointOut = endPointOut;
            _wait = 50;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forceConnect() throws IOException, InterruptedException {
        while (!_sender.isConnected()) {
            _sender.connect(_endPointOut);
            if (_sender.isConnected())
                break;
            Thread.sleep(_wait);
        }
    }

    private void forceAccept() throws IOException, InterruptedException {
        while (_receiverConn == null) {
            _receiverConn = _receiver.accept();
            if (_receiverConn != null)
                break;
            Thread.sleep(_wait);
        }
    }

    private final Object _sendSync = new Object();

    @Override
    public <T> void send(T payload) {
        try {
            synchronized (_sendSync) {
                forceConnect();
                var bytes = _encoding.encode(payload);
                var buff = ByteBuffer.wrap(bytes);
                _sender.write(buff);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Object _receiveSync = new Object();
    private SocketChannel _receiverConn;

    @Override
    public <T> T pull(Class<T> clazz) {
        try {
            synchronized (_receiveSync) {
                forceAccept();
                var buff = NetworkTools.readWithSize(_receiverConn);
                var bytes = buff.array();
                return _encoding.decode(bytes, clazz);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        _encoding.close();
        _sender.close();
        if (_receiverConn != null) _receiverConn.close();
        _receiver.close();
    }
}
