using System;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Client;
using JNetCall.Sharp.Client.Tools;
using JNetCall.Sharp.Impl.Enc;
using JNetCall.Sharp.Server;

namespace JNetCall.Sharp.Tests.IO
{
    public abstract class TransportTest : CallTest
    {
        protected abstract (ISendTransport, ISendTransport) GetBoth();

        protected readonly IEncoding<byte[]> Encoding = new BinaryEncoding();

        protected override string Patch(string input)
        {
            return input.Replace("=255", "=-1")
                .Replace("E+", "E")
                .Replace("=1,", "=1.")
                .Replace("=3,", "=3.")
                .Replace("=True", "=true")
                .Replace("[False", "[false")
                .Replace("[-1,", "[-1.")
                .Replace("[-3,", "[-3.");
        }

        protected override T Create<T>()
        {
            var (left, right) = GetBoth();
            var client = CreateClient<T>(left);
            CreateServer<TestedService>(right, false);
            return client;
        }

        private static T CreateClient<T>(ISendTransport transport)
            where T : class, IDisposable
        {
            var executor = new ThreadExecutor();
            var interceptor = new ClassProxy(transport, executor);
            interceptor.Listen();
            return ClientHelper.Create<T>(interceptor);
        }

        private static void CreateServer<T>(ISendTransport transport, bool blocking)
            where T : new()
        {
            var executor = new ThreadExecutor();
            var instance = new T();
            var hosting = new ClassHosting(instance, transport, executor);
            hosting.RegisterAll();
            if (blocking)
                hosting.ServeAndWait();
            else
                hosting.Serve();
        }
    }
}