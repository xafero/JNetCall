using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using JNetCall.Sharp.API.Flow;
using JNetCall.Sharp.API.IO;
using JNetHotel.Sharp;

namespace JNetCall.Sharp.Client
{
    internal sealed class JvmTransport : IPullTransport
    {
        private readonly string _jar;
        private readonly Timer _timer;
        private readonly BlockingCollection<object> _inputs;
        private readonly BlockingCollection<object> _outputs;

        public JvmTransport(string jar)
        {
            if (!File.Exists(jar))
                throw new FileNotFoundException($"Missing: {jar}");
            _jar = jar;
            _timer = StartTimer(OnTick);
            _inputs = new BlockingCollection<object>();
            _outputs = new BlockingCollection<object>();
        }

        private void OnTick(object _)
        {
            Console.WriteLine(" " + DateTime.Now + " ");

            /*
             *  lock (_sync)
            {
                foreach (var input in inputs) _in.Add(input);
                var copy = new List<MethodResult>();
                while (_out.TryTake(out var item)) copy.Add(item);
                return copy;
            }
            
              public void Intercept(IInvocation invocation)
        {
            using var input = new MemoryStream();
            using var output = new MemoryStream();
            using var proto = new ProtoConvert(input, output, Settings);
            var call = Pack(invocation);
            if (call == null)
                return;
            Write(proto, call);
            var array = output.ToArray();
            var result = SendAndGetArray(array);
            input.Write(result);
            input.Position = 0L;
            var answer = Read<MethodResult>(proto);
            Unpack(invocation, answer);
        }
            
             */
        }

        private static Timer StartTimer(TimerCallback task)
        {
            var periodMs = 500L;
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
        }

        private static readonly object Sync = new();
        private static Jvm _vm;

        private static Jvm SetupJvm(string jar)
        {
            lock (Sync)
            {
                if (_vm != null)
                    return _vm;

                var vmRef = Natives.GetVmRef();
                vmRef.LoadLib();
                var jvm = new Jvm(vmRef, jar);
                return jvm;
            }
        }

        private void Prepare()
        {
            lock (Sync)
            {
                _vm = SetupJvm(_jar);
            }
        }

        private byte[] SendAndGetArray(byte[] input)
        {
            lock (Sync)
            {
                var args = new List<object> { input };
                const string type = "Lx/Boot;";
                var output = _vm.CallStaticMethod<byte[]>(type, "call", "([B)[B", args);
                return output;
            }
        }

        private void Stop(int milliseconds = 250)
        {
            var domain = AppDomain.CurrentDomain;
            domain.ProcessExit += (_, _) =>
            {
                lock (Sync)
                    _vm.Dispose();
            };
        }
    }
}