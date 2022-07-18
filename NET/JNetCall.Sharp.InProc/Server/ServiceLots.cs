using System;
using System.Collections.Generic;
using System.IO;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.API;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Tools;

namespace JNetCall.Sharp.Server
{
    public static class ServiceLots
    {
        public static ClassHosting Create<T>() where T : new()
        {
            var protocol = new NativeHostSink();
            Register(protocol);
            return Create<T>(protocol);
        }

        private static ClassHosting Create<T>(ISendTransport protocol) where T : new()
        {
            var instance = new T();
            var pool = new ThreadExecutor();
            var host = new ClassHosting(instance, protocol, pool);
            return host;
        }

        private static readonly IList<ICaller> Lots = new List<ICaller>();

        private static void Register(ICaller lot)
        {
            Lots.Add(lot);
        }

        // ReSharper disable UnusedMember.Global
        private static byte[] Call(byte[] input)
        {
            foreach (var lot in Lots)
            {
                using var output = new MemoryStream();
                if (!lot.TryCall(input, output))
                    continue;
                return output.ToArray();
            }
            return new[] { unchecked((byte)-1) };
        }

        public static IntPtr Call(IntPtr inputPtr)
        {
            var input = Interop.ToByteArray(inputPtr);
            var output = Call(input);
            return Interop.ToPointer(output);
        }
    }
}