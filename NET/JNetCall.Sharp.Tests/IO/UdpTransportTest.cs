using System.Threading;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Impl.IO.Net;

namespace JNetCall.Sharp.Tests.IO
{
    public sealed class UdpTransportTest : TransportTest
    {
        private static int _offset;
        private static int NextOffset => Interlocked.Increment(ref _offset);

        protected override (ISendTransport, ISendTransport) GetBoth()
        {
            var offset = NextOffset;
            var portOne = 11001 + offset;
            var portTwo = 11051 + offset;
            var left = new UdpTransport(
                Encoding,
                "localhost", portOne,
                "localhost", portTwo
            );
            var right = new UdpTransport(
                Encoding,
                "localhost", portTwo,
                "localhost", portOne
            );
            return (left, right);
        }
    }
}