using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.Flow;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Impl.Enc;

namespace JNetCall.Sharp.Client
{
    internal sealed class JvmTransport : IPullTransport
    {
        private static SingleThread<JvmContainer> _single;

        private readonly object _sync;
        private readonly IEncoding<byte[]> _encoding;
        private readonly Timer _timer;
        private readonly BlockingCollection<object> _inputs;
        private readonly BlockingCollection<object> _outputs;

        public JvmTransport(string jar)
        {
            if (!File.Exists(jar))
                throw new FileNotFoundException($"Missing: {jar}");
            _sync = new object();
            _encoding = new BinaryEncoding();
            _timer = StartTimer(OnTick);
            _inputs = new BlockingCollection<object>();
            _outputs = new BlockingCollection<object>();
            if (_single != null)
                return;
            _single = new SingleThread<JvmContainer>(() => new JvmContainer(jar));
        }

        private void OnTick(object _)
        {
            try
            {
                lock (_sync)
                {
                    var outputs = new List<MethodCall>();
                    while (_outputs.TryTake(out var item)) outputs.Add((MethodCall)item);
                    var output = _encoding.Encode(outputs);
                    var input = SendAndGetArray(output);
                    var inputs = _encoding.Decode<IList<MethodResult>>(input);
                    foreach (var item in inputs) _inputs.Add(item);
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e);
            }
        }

        private byte[] SendAndGetArray(byte[] input)
        {
            lock (_sync)
            {
                var res = new byte[1][];
                var wait = new ManualResetEvent(false);
                _single.Execute(i =>
                {
                    res[0] = i.SendAndGetArray(input);
                    wait.Set();
                });
                wait.WaitOne();
                var bytes = res[0];
                return bytes;
            }
        }

        private static Timer StartTimer(TimerCallback task)
        {
            var periodMs = 15L;
            var timer = new Timer(task);
            timer.Change(periodMs, periodMs);
            return timer;
        }

        public T Pull<T>()
        {
            return (T)_inputs.Take();
        }

        public void Send<T>(T payload)
        {
            _outputs.Add(payload);
        }

        public void Dispose()
        {
            _inputs.Dispose();
            _outputs.Dispose();
            _timer.Dispose();
            _encoding.Dispose();
        }
    }
}