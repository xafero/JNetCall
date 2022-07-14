using System.IO;
using JNetCall.Sharp.API.Enc;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Impl.Enc
{
    public class BinaryEncoding : IByteEncoding
    {
        private readonly ProtoSettings _config;

        public BinaryEncoding()
        {
            _config = new ProtoSettings();
        }

        public byte[] Encode<T>(T data)
        {
            using var output = new MemoryStream();
            using var proto = new ProtoConvert(null, output, _config);
            proto.WriteObject(data);
            return output.ToArray();
        }

        public T Decode<T>(byte[] data)
        {
            using var input = new MemoryStream(data);
            using var proto = new ProtoConvert(input, null, _config);
            var res = proto.ReadObject<T>();
            return res;
        }

        public void Dispose()
        {
        }
    }
}