using System;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.API;
using JNetHotel.Sharp.Data;

namespace JNetHotel.Sharp.Linux
{
    internal unsafe class LinuxVmRef : IVmRef
    {
        private const string DllName = "libjvm.so";

        [DllImport(DllName, CallingConvention = CallingConvention.StdCall)]
        private static extern JniResult JNI_CreateJavaVM(out IntPtr pvm, out IntPtr pEnv, JvmInitArgs* args);

        [DllImport(DllName, CallingConvention = CallingConvention.StdCall)]
        private static extern JniResult JNI_GetCreatedJavaVMs(out IntPtr pvm, int size, [Out] out int size2);

        [DllImport(DllName, CallingConvention = CallingConvention.StdCall)]
        private static extern JniResult JNI_GetDefaultJavaVMInitArgs(JvmInitArgs* args);

        public JniResult CreateVm(out IntPtr pvm, out IntPtr pEnv, JvmInitArgs* args)
            => JNI_CreateJavaVM(out pvm, out pEnv, args);

        public JniResult GetCreatedVms(out IntPtr pvm, int size, out int size2)
            => JNI_GetCreatedJavaVMs(out pvm, size, out size2);

        public JniResult GetDefaultArgs(JvmInitArgs* args)
            => JNI_GetDefaultJavaVMInitArgs(args);

        public string VmDll => DllName;

        public void LoadLib()
        {
            throw new NotImplementedException(DllName);
        }
    }
}