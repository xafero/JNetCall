using System;

namespace JNetProto.Sharp
{
    public interface IDataReader : IDisposable
    {
        int ReadI32();
        long ReadI64();
        float ReadF32();
        double ReadF64();
        decimal ReadF128();
        string ReadUtf8();
    }
}