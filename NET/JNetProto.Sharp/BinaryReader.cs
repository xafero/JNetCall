using System;
using System.Globalization;
using System.IO;
using System.Text;

namespace JNetProto.Sharp
{
    public class BinaryReader : IDataReader
    {
        private readonly CultureInfo _cult;
        private readonly Encoding _enc;
        private readonly Stream _stream;

        public BinaryReader(Stream stream)
        {
            _cult = CultureInfo.InvariantCulture;
            _enc = Encoding.UTF8;
            _stream = stream;
        }

        private byte[] ReadBytes(int size)
        {
            var bytes = new byte[size];
            var length = _stream.Read(bytes);
            if (length != size)
                throw new ArgumentException(length + " ; " + size);
            return bytes;
        }

        public int ReadI32()
        {
            return BitConverter.ToInt32(ReadBytes(4));
        }

        public long ReadI64()
        {
            return BitConverter.ToInt64(ReadBytes(8));
        }

        public float ReadF32()
        {
            return BitConverter.ToSingle(ReadBytes(4));
        }

        public double ReadF64()
        {
            return BitConverter.ToSingle(ReadBytes(8));
        }

        public decimal ReadF128()
        {
            return decimal.Parse(ReadString(), _cult);
        }

        public string ReadUtf8()
        {
            return ReadString();
        }

        private string ReadString()
        {
            return _enc.GetString(ReadBytes(_stream.ReadByte()));
        }

        public void Dispose()
        {
            _stream?.Dispose();
        }
    }
}