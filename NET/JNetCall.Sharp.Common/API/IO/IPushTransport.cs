using System;

namespace JNetCall.Sharp.API.IO
{
    public interface IPushTransport : ISendTransport
    {
        void OnPush<T>(Action<T> data);
    }
}