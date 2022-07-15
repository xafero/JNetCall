using System.IO;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Client.Tools;
using JNetCall.Sharp.Impl.Enc;
using JNetCall.Sharp.Impl.IO;

namespace JNetCall.Sharp.Client
{
    // ReSharper disable once InconsistentNaming
    public static class StdIOClient
    {
        public static T Create<T>(string exe) where T : class
        {
            var pool = new ThreadExecutor();
            var protocol = new JarTransport(exe, InitDefault);
            var handler = new ClassProxy(protocol, pool);
            handler.Listen();
            return ClientHelper.Create<T>(handler);
        }

        private static ISendTransport InitDefault(Stream stdIn, Stream stdOut)
        {
            var enc = new BinaryEncoding();
            return new StreamTransport(enc, stdIn, stdOut);
        }
    }
}