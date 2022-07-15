using System.IO;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Client
{
    public delegate ISendTransport StreamInit(Stream stdIn, Stream stdOut);
}