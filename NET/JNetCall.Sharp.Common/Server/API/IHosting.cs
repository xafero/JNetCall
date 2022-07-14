using System;

namespace JNetCall.Sharp.Server.API
{
    public interface IHosting : IDisposable
    {
        void AddServiceEndpoint(Type type);

        T GoDynInvoke<T>(short id, params object[] args);
    }
}