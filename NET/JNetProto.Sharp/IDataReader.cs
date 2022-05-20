﻿using System;
using System.Collections;

namespace JNetProto.Sharp
{
    public interface IDataReader : IDisposable
    {
        bool ReadBool();
        byte ReadI8();
        short ReadI16();
        int ReadI32();
        long ReadI64();
        float ReadF32();
        double ReadF64();
        decimal ReadF128();
        char ReadChar();
        string ReadUtf8();
        TimeSpan ReadDuration();
        DateTime ReadTimestamp();
        Guid ReadGuid();
        Array ReadArray();
        IDictionary ReadMap();
        object ReadObject();
    }
}