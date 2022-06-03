using System.IO;
using System.Runtime.InteropServices;

namespace JNetCall.Sharp.API
{
    internal interface ICaller
    {
        bool TryCall(byte[] @in, Stream output);
    }

    [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
    public delegate void BootDelegate();

    [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
    public delegate byte[] CallDelegate(byte[] input);
}