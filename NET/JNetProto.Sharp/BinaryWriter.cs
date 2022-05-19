﻿using System;
using System.Globalization;
using System.IO;
using System.Text;

namespace JNetProto.Sharp
{
    public class BinaryWriter : IDataWriter
    {
        private readonly CultureInfo _cult;
        private readonly Encoding _enc;
        private readonly Stream _stream;

        public BinaryWriter(Stream stream)
        {
            _cult = CultureInfo.InvariantCulture;
            _enc = Encoding.UTF8;
            _stream = stream;
        }

        public void WriteU8(byte value)
        {
            _stream.Write(new[] { value });
        }

        public void WriteI8(sbyte value)
        {
            _stream.Write(new[] { (byte)value });
        }

        public void WriteI16(short value)
        {
            _stream.Write(BitConverter.GetBytes(value));
        }

        public void WriteI32(int value)
        {
            _stream.Write(BitConverter.GetBytes(value));
        }

        public void WriteI64(long value)
        {
            _stream.Write(BitConverter.GetBytes(value));
        }

        public void WriteF32(float value)
        {
            _stream.Write(BitConverter.GetBytes(value));
        }

        public void WriteF64(double value)
        {
            _stream.Write(BitConverter.GetBytes(value));
        }

        public void WriteF128(decimal value)
        {
            WriteUtf8(value.ToString(_cult));
        }

        public void WriteUtf8(string value)
        {
            var bytes = _enc.GetBytes(value);
            _stream.WriteByte((byte)bytes.Length);
            _stream.Write(bytes);
        }

        public void WriteDuration(TimeSpan value)
        {
            WriteF64(value.TotalMilliseconds);
        }

        public void WriteTimestamp(DateTime value)
        {
            var date = new DateTimeOffset(value.ToUniversalTime());
            WriteI64(date.ToUnixTimeSeconds());
            WriteI32(int.Parse(date.ToString("fffffff")));
        }

        public void WriteGuid(Guid value)
        {
            _stream.Write(value.ToByteArray());
        }

        public void WriteObject(object value)
        {
            var kind = DataTypes.GetKind(value.GetType());
            _stream.WriteByte((byte)kind);
            switch (kind)
            {
                case DataType.U8: WriteU8((byte)value); break;
                case DataType.I8: WriteI8((sbyte)value); break;
                case DataType.I16: WriteI16((short)value); break;
                case DataType.I32: WriteI32((int)value); break;
                case DataType.I64: WriteI64((long)value); break;
                case DataType.F32: WriteF32((float)value); break;
                case DataType.F64: WriteF64((double)value); break;
                case DataType.F128: WriteF128((decimal)value); break;
                case DataType.UTF8: WriteUtf8((string)value); break;
                case DataType.Duration: WriteDuration((TimeSpan)value); break;
                case DataType.Timestamp: WriteTimestamp((DateTime)value); break;
                case DataType.Guid: WriteGuid((Guid)value); break;
                default: throw new ArgumentException(kind.ToString());
            }
        }

        public void Dispose()
        {
            _stream?.Dispose();
        }
    }
}