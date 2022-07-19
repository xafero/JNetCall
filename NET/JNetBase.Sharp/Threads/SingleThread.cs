using System;
using System.Collections.Concurrent;
using System.Threading;

namespace JNetBase.Sharp.Threads
{
    public sealed class SingleThread<T> : IDisposable
        where T : IDisposable
    {
        private readonly Func<T> _creator;
        private readonly BlockingCollection<Action<T>> _actions;
        private readonly Thread _thread;

        private bool _running;

        public SingleThread(Func<T> creator)
        {
            _creator = creator;
            _actions = new BlockingCollection<Action<T>>();
            _running = true;
            _thread = new Thread(DoLoop)
            {
                IsBackground = true
            };
            _thread.Start();
        }

        public void Dispose()
        {
            _running = false;
            _thread.Interrupt();
            _actions.Dispose();
        }

        ~SingleThread()
        {
            Dispose();
        }

        public void Execute(Action<T> action)
        {
            _actions.Add(action);
        }

        private void DoLoop()
        {
            using var instance = _creator();
            while (_running)
            {
                var action = _actions.Take();
                action(instance);
            }
        }
    }
}