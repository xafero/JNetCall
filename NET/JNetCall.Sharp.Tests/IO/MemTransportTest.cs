using System.IO;
using JNetBase.Sharp.IO;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Impl.IO;

namespace JNetCall.Sharp.Tests.IO
{
    public sealed class MemTransportTest : TransportTest
    {
        protected override (ISendTransport, ISendTransport) GetBoth()
        {
            Stream mem11001 = new MemPipeStream();
            Stream mem11002 = new MemPipeStream();
            var left = new StreamTransport(
                Encoding,
                mem11001,
                mem11002
            );
            var right = new StreamTransport(
                Encoding,
                mem11002,
                mem11001
            );
            return (left, right);
        }
    }
}