using System;

namespace JNetCall.Sharp.API.IO
{
    public interface ISendTransport : IDisposable
    {
        void Send<T>(T payload);
    }
}