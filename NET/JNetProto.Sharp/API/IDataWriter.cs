using System;
using System.Collections;
using System.Runtime.CompilerServices;

namespace JNetProto.Sharp.API
{
    public interface IDataWriter : IDisposable
    {
        void WriteBool(bool value);
        void WriteI8(byte value);
        void WriteI16(short value);
        void WriteI32(int value);
        void WriteI64(long value);
        void WriteF32(float value);
        void WriteF64(double value);
        void WriteF128(decimal value);
        void WriteChar(char value);
        void WriteUtf8(string value);
        void WriteDuration(TimeSpan value);
        void WriteTimestamp(DateTime value);
        void WriteGuid(Guid value);
        void WriteArray(Array value);
        void WriteMap(IDictionary value);
        void WriteTuple(ITuple value);
        void WriteSet(IEnumerable value);
        void WriteList(IList value);
        void WriteBag(object[] value);
        void WriteBinary(byte[] value);
        void WriteNull();
        void WriteObject(object value);
        void Flush();
    }
}