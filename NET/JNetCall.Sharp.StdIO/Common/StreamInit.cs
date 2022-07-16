using System.IO;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Common
{
    public delegate ISendTransport StreamInit(Stream stdIn, Stream stdOut);
}