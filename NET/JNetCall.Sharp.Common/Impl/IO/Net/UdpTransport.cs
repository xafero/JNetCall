using System.Net;
using System.Net.Sockets;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Impl.IO.Net
{
    public sealed class UdpTransport : IPullTransport
    {
        private readonly IEncoding<byte[]> _encoding;
        private readonly UdpClient _receiver;
        private readonly UdpClient _sender;

        public UdpTransport(IEncoding<byte[]> encoding,
            string hostIn, int portIn, string hostOut, int portOut)
            : this(encoding, NetworkTools.ToEndPoint(hostIn, portIn),
                NetworkTools.ToEndPoint(hostOut, portOut))
        {
        }

        private UdpTransport(IEncoding<byte[]> encoding,
            IPEndPoint endPointIn, IPEndPoint endPointOut)
        {
            _encoding = encoding;
            _receiver = new UdpClient(endPointIn);
            _sender = new UdpClient();
            _sender.Connect(endPointOut);
        }

        public void Send<T>(T payload)
        {
            var bytes = _encoding.Encode(payload);
            _sender.Send(bytes, bytes.Length);
        }

        private IPEndPoint _remote;

        public T Pull<T>()
        {
            var bytes = _receiver.Receive(ref _remote);
            return _encoding.Decode<T>(bytes);
        }

        public void Dispose()
        {
            _encoding.Dispose();
            _sender.Close();
            _sender.Dispose();
            _receiver.Close();
            _receiver.Dispose();
        }
    }
}