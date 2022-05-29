using System;
using System.IO;
using System.Linq;
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
            var javaBinLink = new FileInfo("/usr/bin/java");
            var javaBinPath = javaBinLink.ResolveLinkTarget(true)?.FullName;
            if (!File.Exists(javaBinPath))
                throw new InvalidOperationException("Could not locate Java binary!");
            var javaRootPath = Path.GetFullPath(Path.Combine(javaBinPath, "..", ".."));
            if (!Directory.Exists(javaRootPath))
                throw new InvalidOperationException("Could not locate Java root!");
            var javaSoPath = Directory.GetFiles(javaRootPath, "libjvm.so", SearchOption.AllDirectories)
                .FirstOrDefault();
            if (!File.Exists(javaSoPath))
                throw new InvalidOperationException("Could not locate JVM library!");
            UnixLoadLibrary(javaSoPath, RTLD_NOW);
        }

        private const int RTLD_NOW = 0x002;

        [DllImport("libdl.so.2", EntryPoint = "dlopen")]
        public static extern IntPtr UnixLoadLibrary(string fileName, int flags);
    }
}