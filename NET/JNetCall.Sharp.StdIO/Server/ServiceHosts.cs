using System;
using System.IO;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Common;
using JNetCall.Sharp.Impl.Enc;
using JNetCall.Sharp.Impl.IO;

namespace JNetCall.Sharp.Server
{
    public static class ServiceHosts
    {
        public static ClassHosting Create<T>() where T : new()
        {
            var stdIn = Console.OpenStandardInput();
            var stdOut = Console.OpenStandardOutput();
            ByteMarks.WriteSync(stdIn, stdOut);
            var protocol = InitDefault(stdIn, stdOut);
            return Create<T>(protocol);
        }

        private static ISendTransport InitDefault(Stream stdIn, Stream stdOut)
        {
            var enc = new BinaryEncoding();
            return new StreamTransport(enc, stdIn, stdOut);
        }

        private static ClassHosting Create<T>(ISendTransport protocol) where T : new()
        {
            var instance = new T();
            var pool = new ThreadExecutor();
            var host = new ClassHosting(instance, protocol, pool);
            return host;
        }
    }
}