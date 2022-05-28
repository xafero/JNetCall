using System;
using System.Runtime.InteropServices;

// ReSharper disable InconsistentNaming

namespace JNetHotel.Sharp.Interop
{
    public struct JNINativeMethod : IDisposable
    {
        public IntPtr name; // char* 
        public IntPtr signature; // char* 
        public IntPtr fnPtr; // void* 

        public void Dispose()
        {
            if (name != IntPtr.Zero)
            {
                Marshal.FreeHGlobal(name);
                name = IntPtr.Zero;
            }
            if (signature != IntPtr.Zero)
            {
                Marshal.FreeHGlobal(signature);
                signature = IntPtr.Zero;
            }
        }
    }
}