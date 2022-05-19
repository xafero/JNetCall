﻿using System;
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

        public byte ReadU8()
        {
            return ReadBytes(1)[0];
        }

        public sbyte ReadI8()
        {
            return (sbyte)ReadBytes(1)[0];
        }

        public short ReadI16()
        {
            return BitConverter.ToInt16(ReadBytes(2));
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
            return BitConverter.ToDouble(ReadBytes(8));
        }

        public decimal ReadF128()
        {
            return decimal.Parse(ReadUtf8(), _cult);
        }

        public string ReadUtf8()
        {
            return _enc.GetString(ReadBytes(_stream.ReadByte()));
        }

        public TimeSpan ReadDuration()
        {
            return TimeSpan.FromMilliseconds(ReadF64());
        }

        public DateTime ReadTimestamp()
        {
            var date = DateTimeOffset.FromUnixTimeSeconds(ReadI64()).UtcDateTime;
            return date.AddTicks(ReadI32());
        }

        public Guid ReadGuid()
        {
            return new Guid(ReadBytes(16));
        }

        public object ReadObject()
        {
            var kind = (DataType)_stream.ReadByte();
            switch (kind)
            {
                case DataType.U8: return ReadU8();
                case DataType.I8: return ReadI8();
                case DataType.I16: return ReadI16();
                case DataType.I32: return ReadI32();
                case DataType.I64: return ReadI64();
                case DataType.F32: return ReadF32();
                case DataType.F64: return ReadF64();
                case DataType.F128: return ReadF128();
                case DataType.UTF8: return ReadUtf8();
                case DataType.Duration: return ReadDuration();
                case DataType.Timestamp: return ReadTimestamp();
                case DataType.Guid: return ReadGuid();
                default: throw new ArgumentException(kind.ToString());
            }
        }

        public void Dispose()
        {
            _stream?.Dispose();
        }
    }
}