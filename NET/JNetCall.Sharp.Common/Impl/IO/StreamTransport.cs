using System;
using System.IO;
using JNetBase.Sharp.IO;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Impl.IO
{
    public sealed class StreamTransport : ISendTransport, IPullTransport, IDisposable
    {
        private readonly IEncoding<byte[]> _encoding;
        private readonly Stream _streamIn;
        private readonly Stream _streamOut;

        public StreamTransport(IEncoding<byte[]> encoding,
            Stream streamIn, Stream streamOut)
        {
            _encoding = encoding;
            _streamIn = streamIn;
            _streamOut = streamOut;
        }

        public void Send<T>(T payload)
        {
            var bytes = _encoding.Encode(payload);
            _streamOut.Write(bytes, 0, bytes.Length);
            _streamOut.Flush();
            if (_streamOut is IRewindable r)
                r.Rewind(bytes.Length);
        }

        public T Pull<T>()
        {
            var bytes = StreamTools.ReadWithSize(_streamIn);
            return _encoding.Decode<T>(bytes);
        }

        public void Dispose()
        {
            _streamOut.Dispose();
            _streamIn.Dispose();
            _encoding.Dispose();
        }
    }
}