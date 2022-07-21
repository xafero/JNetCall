using System;

namespace JNetCall.Sharp.API.IO
{
    public interface IErrorable : IDisposable
    {
        string GetErrorDetails();
    }
}