using System;
using System.IO;
using System.Runtime.InteropServices;

namespace JNetCall.Sharp.API
{
    internal interface ICaller
    {
        bool TryCall(byte[] @in, Stream output);
    }

    // ReSharper disable UnusedMember.Global

    [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
    public delegate IntPtr CallDelegate(IntPtr input);
}