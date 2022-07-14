using System;
using System.IO;
using System.Threading.Tasks;

namespace JNetCall.Sharp.Impl.IO
{
    public static class StreamTools
    {
        private static async Task<byte[]> TryReadAsync(Stream stream, int size, byte[] prefix = null)
        {
            var skip = prefix?.Length ?? 0;
            size += skip;
            var buffer = new byte[size];
            int got;
            if (prefix == null)
            {
                got = await stream.ReadAsync(buffer);
            }
            else
            {
                Array.Copy(prefix, 0, buffer, 0, skip);
                var tmp = await stream.ReadAsync(buffer, skip, size - skip);
                got = tmp + skip;
            }
            if (size != got)
            {
                throw new InvalidOperationException(size + " != " + got);
            }
            return buffer;
        }

        public static async Task<byte[]> ReadWithSizeAsync(Stream stream)
        {
            var sizeBytes = await TryReadAsync(stream, 4);
            var size = BitConverter.ToInt32(sizeBytes);
            var bytes = await TryReadAsync(stream, size, sizeBytes);
            return bytes;
        }

        private static byte[] TryRead(Stream stream, int size, byte[] prefix = null)
        {
            var skip = prefix?.Length ?? 0;
            size += skip;
            var buffer = new byte[size];
            int got;
            if (prefix == null)
            {
                got = stream.Read(buffer);
            }
            else
            {
                Array.Copy(prefix, 0, buffer, 0, skip);
                var tmp = stream.Read(buffer, skip, size - skip);
                got = tmp + skip;
            }
            if (size != got)
            {
                throw new InvalidOperationException(size + " != " + got);
            }
            return buffer;
        }

        public static byte[] ReadWithSize(Stream stream)
        {
            var sizeBytes = TryRead(stream, 4);
            var size = BitConverter.ToInt32(sizeBytes);
            var bytes = TryRead(stream, size, sizeBytes);
            return bytes;
        }
    }
}