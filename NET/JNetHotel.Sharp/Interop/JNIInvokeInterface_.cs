// ReSharper disable InconsistentNaming
using System;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.Data;

namespace JNetHotel.Sharp.Interop
{
    public struct JNIInvokeInterface_
    {
        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int DestroyJavaVM(IntPtr pVM);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal unsafe delegate int AttachCurrentThread(IntPtr pVM, out IntPtr pEnv, JvmInitArgs* Args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int DetachCurrentThread(IntPtr pVM);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetEnv(IntPtr pVM, out IntPtr pEnv, int Version);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal unsafe delegate int AttachCurrentThreadAsDaemon(IntPtr pVM, out IntPtr pEnv, JvmInitArgs* Args);
    }
}