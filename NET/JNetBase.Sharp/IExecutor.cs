using System;
using System.Threading;

namespace JNetBase.Sharp
{
    public interface IExecutor : IDisposable
    {
        Thread CreateThread(ThreadStart action);
    }
}