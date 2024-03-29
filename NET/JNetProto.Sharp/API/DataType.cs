﻿// ReSharper disable InconsistentNaming
namespace JNetProto.Sharp.API
{
    public enum DataType
    {
        Unknown = 0,

        Bool,
        I8,
        I16,
        I32,
        I64,
        F32,
        F64,
        F128,
        Char,
        UTF8,
        Duration,
        Timestamp,
        Guid,
        Array,
        Map,
        Tuple,
        Set,
        List,
        Bag,
        Binary,
        Null
    }
}