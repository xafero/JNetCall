using System;
using System.Collections;

namespace JNetProto.Sharp
{
    public static class DataTypes
    {
        public static Type GetClass(DataType type)
        {
            switch (type)
            {
                case DataType.Bool: return typeof(bool);
                case DataType.I8: return typeof(byte);
                case DataType.I16: return typeof(short);
                case DataType.I32: return typeof(int);
                case DataType.I64: return typeof(long);
                case DataType.F32: return typeof(float);
                case DataType.F64: return typeof(double);
                case DataType.F128: return typeof(decimal);
                case DataType.Char: return typeof(char);
                case DataType.UTF8: return typeof(string);
                case DataType.Duration: return typeof(TimeSpan);
                case DataType.Timestamp: return typeof(DateTime);
                case DataType.Guid: return typeof(Guid);
                default: throw new ArgumentException(type.ToString());
            }
        }

        public interface IDataType { DataType Kind { get; } }
        public record SingleDt(DataType Kind) : IDataType;
        public record ArrayDt(DataType Kind, int Rank, IDataType Item) : IDataType;
        public record MapDt(DataType Kind, IDataType Key, IDataType Val) : IDataType;

        public static IDataType GetKind(object instance)
        {
            var type = instance as Type ?? instance.GetType();
            if (type.IsArray)
            {
                var rank = type.GetArrayRank();
                var item = type.GetElementType();
                return new ArrayDt(DataType.Array, rank, GetKind(item));
            }
            if (type.IsAssignableTo(typeof(IDictionary)))
            {
                var dict = (IDictionary)instance;
                var f = default(DictionaryEntry);
                foreach (DictionaryEntry entry in dict)
                {
                    f = entry;
                    break;
                }
                return new MapDt(DataType.Map, GetKind(f.Key), GetKind(f.Value));
            }
            return new SingleDt(GetSingleKind(type));
        }

        private static DataType GetSingleKind(Type type)
        {
            switch (type.FullName)
            {
                case "System.Boolean": return DataType.Bool;
                case "System.Byte": return DataType.I8;
                case "System.Int16": return DataType.I16;
                case "System.Int32": return DataType.I32;
                case "System.Int64": return DataType.I64;
                case "System.Single": return DataType.F32;
                case "System.Double": return DataType.F64;
                case "System.Decimal": return DataType.F128;
                case "System.Char": return DataType.Char;
                case "System.String": return DataType.UTF8;
                case "System.TimeSpan": return DataType.Duration;
                case "System.DateTime": return DataType.Timestamp;
                case "System.Guid": return DataType.Guid;
                default: throw new ArgumentException(type.ToString());
            }
        }
    }
}