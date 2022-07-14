using System.Collections.Generic;
using System.Threading;

namespace JNetBase.Sharp.Threads
{
    public sealed class ThreadExecutor : IExecutor
    {
        private readonly IList<Thread> _threads;

        public ThreadExecutor()
        {
            _threads = new List<Thread>();
        }

        public void Dispose()
        {
            foreach (var thread in _threads)
                thread.Interrupt();
            _threads.Clear();
        }

        public Thread CreateThread(ThreadStart action, string name)
        {
            var task = new Thread(action)
            {
                IsBackground = true
            };
            if (name != null)
                task.Name = name;
            _threads.Add(task);
            task.Start();
            return task;
        }
    }
}