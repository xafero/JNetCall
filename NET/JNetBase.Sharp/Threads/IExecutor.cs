using System;
using System.Threading;

namespace JNetBase.Sharp.Threads
{
    public interface IExecutor : IDisposable
    {
        Thread CreateThread(ThreadStart action, string name);
    }
}