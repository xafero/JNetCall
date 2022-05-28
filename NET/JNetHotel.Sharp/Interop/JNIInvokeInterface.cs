// ReSharper disable InconsistentNaming
using System;
using System.Runtime.InteropServices;

namespace JNetHotel.Sharp.Interop
{
    [StructLayout(LayoutKind.Sequential)]
    public struct JNIInvokeInterface
    {
        public IntPtr reserved0;
        public IntPtr reserved1;
        public IntPtr reserved2;

        public IntPtr DestroyJavaVM;
        public IntPtr AttachCurrentThread;
        public IntPtr DetachCurrentThread;
        public IntPtr GetEnv;
        public IntPtr AttachCurrentThreadAsDaemon;
    }
}