using System.Threading;
using JNetBase.Sharp.Threads;
using Nito.AsyncEx;

namespace JNetCall.Sharp.Client.Model
{
    public class CallState
    {
        public ManualResetEvent SyncWait;

        public AsyncManualResetEvent AsyncWait;

        public object Result;

        public void Set()
        {
            SyncWait?.Set();
            AsyncWait?.Set();
        }
    }
}