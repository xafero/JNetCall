using System;

namespace JNetProto.Sharp
{
    public static class DataTypes
    {
        public static Type GetClass(DataType type)
        {
            switch (type)
            {
                case DataType.Bool: return typeof(bool);
                case DataType.U8: return typeof(byte);
                case DataType.I8: return typeof(sbyte);
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

        public static DataType GetKind(Type type)
        {
            switch (type.FullName)
            {
                case "System.Boolean": return DataType.Bool;
                case "System.Byte": return DataType.U8;
                case "System.SByte": return DataType.I8;
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