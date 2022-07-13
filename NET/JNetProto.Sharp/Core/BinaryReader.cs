using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using JNetProto.Sharp.API;

namespace JNetProto.Sharp.Core
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
            {
                var hex = bytes.Length <= 1024
                    ? BitConverter.ToString(bytes).Replace("-", "")
                    : "(too big for hex)";
                throw new ArgumentException($"Got {length} B instead of {size}! {hex}");
            }
            return bytes;
        }

        public bool ReadBool()
        {
            return ReadI8() == 1;
        }

        public byte ReadI8()
        {
            return ReadBytes(1)[0];
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
            return decimal.Parse(ReadUtf8(wide: false), _cult);
        }

        public char ReadChar()
        {
            return (char)ReadI16();
        }

        public string ReadUtf8()
        {
            return ReadUtf8(wide: true);
        }

        private string ReadUtf8(bool wide)
        {
            var size = wide ? ReadI16() : ReadI8();
            return _enc.GetString(ReadBytes(size));
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

        public Array ReadArray()
        {
            var item = (DataType)_stream.ReadByte();
            var rank = _stream.ReadByte();
            var lengths = new int[rank];
            for (var i = 0; i < rank; i++)
                lengths[i] = ReadI32();
            var clazz = DataTypes.GetClass(item);
            var array = Array.CreateInstance(clazz, lengths);
            var indices = new int[rank];
            for (var i = 0; i < rank; i++)
            for (var j = 0; j < lengths[i]; j++)
            {
                var obj = ReadObject(item);
                indices[i] = j;
                array.SetValue(obj, indices);
            }
            return array;
        }

        public IDictionary ReadMap()
        {
            var keyKind = (DataType)_stream.ReadByte();
            var valKind = (DataType)_stream.ReadByte();
            var size = ReadI32();
            var keyClass = DataTypes.GetClass(keyKind);
            var valClass = DataTypes.GetClass(valKind);
            var mapClass = typeof(SortedDictionary<,>).MakeGenericType(keyClass, valClass);
            var map = (IDictionary)Activator.CreateInstance(mapClass)!;
            for (var i = 0; i < size; i++)
            {
                var key = ReadObject(keyKind);
                var val = ReadObject(valKind);
                map[key] = val;
            }
            return map;
        }

        public ITuple ReadTuple()
        {
            var size = ReadI8();
            var args = new object[size];
            var types = new Type[size];
            for (var i = 0; i < size; i++)
            {
                var obj = ReadObject();
                args[i] = obj;
                types[i] = obj.GetType();
            }
            var method = typeof(Tuple).GetMethods()
                .First(m => m.Name == nameof(Tuple.Create) && m.GetParameters().Length == types.Length);
            var tuple = (ITuple)method.MakeGenericMethod(types).Invoke(null, args);
            return tuple;
        }

        public object[] ReadBag()
        {
            var size = ReadI8();
            var args = new object[size];
            for (var i = 0; i < size; i++)
            {
                var obj = ReadObject();
                args[i] = obj;
            }
            return args;
        }

        public byte[] ReadBinary()
        {
            var size = ReadI32();
            return ReadBytes(size);
        }

        public object ReadNull()
        {
            return null;
        }

        public IEnumerable ReadSet()
        {
            var setType = typeof(SortedSet<>);
            return ReadEnumerable(setType);
        }

        public IList ReadList()
        {
            var listType = typeof(List<>);
            return (IList)ReadEnumerable(listType);
        }

        private IEnumerable ReadEnumerable(Type type)
        {
            var valKind = (DataType)_stream.ReadByte();
            var size = ReadI32();
            var valClass = DataTypes.GetClass(valKind);
            var genType = type.MakeGenericType(valClass);
            var coll = (IEnumerable)Activator.CreateInstance(genType)!;
            var adder = genType.GetMethod("Add", new[] { valClass })!;
            for (var i = 0; i < size; i++)
            {
                var val = ReadObject(valKind);
                adder.Invoke(coll, new[] { val });
            }
            return coll;
        }

        public object ReadObject()
        {
            var kind = (DataType)_stream.ReadByte();
            return ReadObject(kind);
        }

        private object ReadObject(DataType kind)
        {
            switch (kind)
            {
                case DataType.Bool: return ReadBool();
                case DataType.I8: return ReadI8();
                case DataType.I16: return ReadI16();
                case DataType.I32: return ReadI32();
                case DataType.I64: return ReadI64();
                case DataType.F32: return ReadF32();
                case DataType.F64: return ReadF64();
                case DataType.F128: return ReadF128();
                case DataType.Char: return ReadChar();
                case DataType.UTF8: return ReadUtf8();
                case DataType.Duration: return ReadDuration();
                case DataType.Timestamp: return ReadTimestamp();
                case DataType.Guid: return ReadGuid();
                case DataType.Array: return ReadArray();
                case DataType.Map: return ReadMap();
                case DataType.Tuple: return ReadTuple();
                case DataType.Set: return ReadSet();
                case DataType.List: return ReadList();
                case DataType.Bag: return ReadBag();
                case DataType.Binary: return ReadBinary();
                case DataType.Null: return ReadNull();
                default: throw new ArgumentException($"{nameof(ReadObject)} {kind}");
            }
        }

        public void Dispose()
        {
            _stream?.Dispose();
        }
    }
}
