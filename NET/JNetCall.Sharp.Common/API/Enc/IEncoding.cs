using System;

namespace JNetCall.Sharp.API.Enc
{
    public interface IEncoding<TRaw> : IDisposable
    {
        TRaw Encode<T>(T data);

        T Decode<T>(TRaw data);
    }
}