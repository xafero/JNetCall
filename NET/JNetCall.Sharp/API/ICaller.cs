using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JNetCall.Sharp.API
{
    internal interface ICaller
    {
        bool TryCall(byte[] @in, Stream output);
    }
}