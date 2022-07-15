using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Impl.IO.Net
{
    public sealed class TcpTransport : ISendTransport, IPullTransport, IDisposable
    {
        private readonly IEncoding<byte[]> _encoding;
        private readonly TcpListener _receiver;
        private readonly TcpClient _sender;
        private readonly IPEndPoint _endPointOut;
        private readonly int _wait;

        public TcpTransport(IEncoding<byte[]> encoding,
            string hostIn, int portIn, string hostOut, int portOut)
            : this(encoding, NetworkTools.ToEndPoint(hostIn, portIn),
                NetworkTools.ToEndPoint(hostOut, portOut))
        {
        }

        private TcpTransport(IEncoding<byte[]> encoding,
            IPEndPoint endPointIn, IPEndPoint endPointOut)
        {
            _encoding = encoding;
            _receiver = new TcpListener(endPointIn);
            _receiver.Start();
            _sender = new TcpClient();
            _endPointOut = endPointOut;
            _wait = 50;
        }

        private void ForceConnect()
        {
            while (!_sender.Connected)
            {
                _sender.Connect(_endPointOut);
                if (_sender.Connected)
                    break;
                Thread.Sleep(_wait);
            }
        }

        private void ForceAccept()
        {
            while (_receiverConn == null)
            {
                _receiverConn = _receiver.AcceptTcpClient();
                if (_receiverConn != null)
                    break;
                Thread.Sleep(_wait);
            }
        }

        private readonly object _sendSync = new();

        public void Send<T>(T payload)
        {
            lock (_sendSync)
            {
                ForceConnect();
                var bytes = _encoding.Encode(payload);
                _sender.GetStream().Write(bytes, 0, bytes.Length);
            }
        }

        private readonly object _receiveSync = new();
        private TcpClient _receiverConn;

        public T Pull<T>()
        {
            lock (_receiveSync)
            {
                ForceAccept();
                var bytes = StreamTools.ReadWithSize(_receiverConn.GetStream());
                return _encoding.Decode<T>(bytes);
            }
        }

        public void Dispose()
        {
            _encoding.Dispose();
            _sender.Close();
            _sender.Dispose();
            _receiverConn?.Close();
            _receiverConn?.Dispose();
            _receiver.Stop();
        }
    }
}