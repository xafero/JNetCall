using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using JNetCall.Sharp.API;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.Flow;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Impl.Enc;

namespace JNetCall.Sharp.Server
{
    public sealed class NativeHostSink : ICaller, IPullTransport
    {
        private readonly object _sync;
        private readonly IEncoding<byte[]> _encoding;
        private readonly BlockingCollection<object> _inputs;
        private readonly BlockingCollection<object> _outputs;

        public NativeHostSink()
        {
            _sync = new object();
            _encoding = new BinaryEncoding();
            _inputs = new BlockingCollection<object>();
            _outputs = new BlockingCollection<object>();
        }

        public T Pull<T>()
        {
            return (T)_inputs.Take();
        }

        public void Send<T>(T payload)
        {
            _outputs.Add(payload);
        }

        private IList<MethodResult> Synchronize(IEnumerable<MethodCall> inputs)
        {
            lock (_sync)
            {
                foreach (var input in inputs) _inputs.Add(input);
                var copy = new List<MethodResult>();
                while (_outputs.TryTake(out var item)) copy.Add((MethodResult)item);
                return copy;
            }
        }

        public bool TryCall(byte[] @in, Stream output)
        {
            var calls = _encoding.Decode<IList<MethodCall>>(@in);
            var answers = Synchronize(calls);
            var bytes = _encoding.Encode(answers);
            output.Write(bytes);
            output.Flush();
            return true;
        }

        public void Dispose()
        {
            _inputs.Dispose();
            _outputs.Dispose();
            _encoding.Dispose();
        }
    }
}