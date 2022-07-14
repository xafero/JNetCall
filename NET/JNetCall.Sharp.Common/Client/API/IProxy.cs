using System;
using Castle.DynamicProxy;

namespace JNetCall.Sharp.Client.API
{
    public interface IProxy : IInterceptor, IDisposable
    {
    }
}