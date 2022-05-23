using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using JNetProto.Sharp.API;

namespace JNetProto.Sharp.Core
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

        public void WriteBool(bool value)
        {
            WriteI8((byte)(value ? 1 : 0));
        }

        public void WriteI8(byte value)
        {
            _stream.Write(new[] { value });
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
            var raw = value.ToString(_cult);
            WriteUtf8(raw, wide: false);
        }

        public void WriteChar(char value)
        {
            WriteI16((short)value);
        }

        public void WriteUtf8(string value)
        {
            WriteUtf8(value, wide: true);
        }

        private void WriteUtf8(string value, bool wide)
        {
            var bytes = _enc.GetBytes(value);
            if (wide)
                WriteI16((short)bytes.Length);
            else
                WriteI8((byte)bytes.Length);
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

        public void WriteArray(Array value)
        {
            for (var dim = 0; dim < value.Rank; dim++)
                WriteI32(value.GetLength(dim));
            foreach (var item in value)
                WriteObject(item, true);
        }

        public void WriteMap(IDictionary value)
        {
            WriteI32(value.Count);
            foreach (DictionaryEntry entry in value)
            {
                WriteObject(entry.Key, true);
                WriteObject(entry.Value, true);
            }
        }

        public void WriteSet(IEnumerable value)
        {
            WriteEnumerable(value, true);
        }

        public void WriteList(IList value)
        {
            WriteEnumerable(value, true);
        }

        public void WriteBag(object[] value)
        {
            WriteI8((byte)value.Length);
            foreach (var item in value)
                WriteObject(item, false);
        }

        public void WriteBinary(byte[] value)
        {
            WriteI32(value.Length);
            _stream.Write(value);
        }

        private void WriteEnumerable(IEnumerable raw, bool skipHeader)
        {
            int count;
            IEnumerable values;
            if (raw is ICollection coll)
            {
                count = coll.Count;
                values = raw;
            }
            else
            {
                var t = raw.GetType();
                if (t.GetProperty(nameof(ICollection.Count))?.GetValue(raw) is int c)
                {
                    count = c;
                    values = raw;
                }
                else
                {
                    var array = raw.OfType<object>().ToArray();
                    count = array.Length;
                    values = array;
                }
            }
            WriteI32(count);
            foreach (var entry in values)
                WriteObject(entry, skipHeader);
        }

        public void WriteTuple(ITuple value)
        {
            WriteI8((byte)value.Length);
            for (var i = 0; i < value.Length; i++)
            {
                WriteObject(value[i], false);
            }
        }
        
        public void WriteObject(object value)
        {
            WriteObject(value, false);
        }

        private void WriteObject(object value, bool skipHeader)
        {
            var kind = DataTypes.GetKind(value);
            if (!skipHeader)
            {
                _stream.WriteByte((byte)kind.Kind);
                if (kind is DataTypes.ArrayDt adt)
                {
                    _stream.WriteByte((byte)adt.Item.Kind);
                    _stream.WriteByte((byte)adt.Rank);
                }
                else if (kind is DataTypes.MapDt mdt)
                {
                    _stream.WriteByte((byte)mdt.Key.Kind);
                    _stream.WriteByte((byte)mdt.Val.Kind);
                }
                else if (kind is DataTypes.ListDt ldt)
                {
                    _stream.WriteByte((byte)ldt.Item.Kind);
                }
            }
            switch (kind.Kind)
            {
                case DataType.Bool: WriteBool((bool)value); break;
                case DataType.I8: WriteI8((byte)value); break;
                case DataType.I16: WriteI16((short)value); break;
                case DataType.I32: WriteI32((int)value); break;
                case DataType.I64: WriteI64((long)value); break;
                case DataType.F32: WriteF32((float)value); break;
                case DataType.F64: WriteF64((double)value); break;
                case DataType.F128: WriteF128((decimal)value); break;
                case DataType.Char: WriteChar((char)value); break;
                case DataType.UTF8: WriteUtf8((string)value); break;
                case DataType.Duration: WriteDuration((TimeSpan)value); break;
                case DataType.Timestamp: WriteTimestamp((DateTime)value); break;
                case DataType.Guid: WriteGuid((Guid)value); break;
                case DataType.Array: WriteArray((Array)value); break;
                case DataType.Map: WriteMap((IDictionary)value); break;
                case DataType.Tuple: WriteTuple((ITuple)value); break;
                case DataType.Set: WriteSet((IEnumerable)value); break;
                case DataType.List: WriteList((IList)value); break;
                case DataType.Bag: WriteBag((object[])value); break;
                case DataType.Binary: WriteBinary((byte[])value); break;
                default: throw new ArgumentException(kind.ToString());
            }
        }

        public void Flush()
        {
            _stream.Flush();
        }

        public void Dispose()
        {
            _stream?.Dispose();
        }
    }
}