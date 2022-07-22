using System.IO;
using System.Threading;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Impl.IO.Disk;

namespace JNetCall.Sharp.Tests.IO
{
    public sealed class FileTransportTest : TransportTest
    {
        private const string Folder = @"FileTmp";

        private static int _offset;
        private static int NextOffset => Interlocked.Increment(ref _offset);

        protected override (ISendTransport, ISendTransport) GetBoth()
        {
            var offset = NextOffset;
            var portOne = 13001 + offset + "";
            var portTwo = 13051 + offset + "";
            var left = new FolderTransport(
                Encoding,
                Path.Combine(Folder, portOne),
                Path.Combine(Folder, portTwo)
            );
            var right = new FolderTransport(
                Encoding,
                Path.Combine(Folder, portTwo),
                Path.Combine(Folder, portOne)
            );
            return (left, right);
        }

        protected override int MaxListWait => 20;
    }
}