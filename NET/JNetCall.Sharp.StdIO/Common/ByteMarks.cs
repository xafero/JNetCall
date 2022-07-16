using System.IO;

namespace JNetCall.Sharp.Common
{
    public static class ByteMarks
    {
        public static (Stream stdIn, Stream stdOut) WriteSync(Stream stdOut, Stream stdIn)
        {
            const int marker = 0xEE;
            // Send flag
            stdIn.WriteByte(marker);
            stdIn.Flush();
            // Receive flag
            while (stdOut.ReadByte() != marker)
            {
            }
            // Ready!
            return (stdOut, stdIn);
        }
    }
}