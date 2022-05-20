using System;
using System.Collections;

namespace JNetProto.Sharp
{
    public interface IDataWriter : IDisposable
    {
        void WriteBool(bool value);
        void WriteU8(byte value);
        void WriteI8(sbyte value);
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
        void WriteObject(object value);
    }
}