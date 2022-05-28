using System;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.API;
using JNetHotel.Sharp.Data;

namespace JNetHotel.Sharp.Windows
{
    internal unsafe class WinVmRef : IVmRef
    {
        private const string DllName = "jvm.dll";

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
            var paths = Environment.GetEnvironmentVariable("Path")?.Split(";");
            var javaBinPath = paths?.FirstOrDefault(p => File.Exists(Path.Combine(p, "java.exe")));
            if (!Directory.Exists(javaBinPath))
                throw new InvalidOperationException("Could not locate Java binary!");
            var javaRootPath = Path.GetDirectoryName(javaBinPath);
            if (!Directory.Exists(javaRootPath))
                throw new InvalidOperationException("Could not locate Java root!");
            var javaDllPath = Directory.GetFiles(javaRootPath, "jvm.dll", SearchOption.AllDirectories).FirstOrDefault();
            if (!File.Exists(javaDllPath))
                throw new InvalidOperationException("Could not locate JVM library!");
            LoadLibrary(javaDllPath);
        }

        [DllImport("kernel32.dll", SetLastError = true, CharSet = CharSet.Unicode)]
        public static extern IntPtr LoadLibrary(string dllToLoad);
    }
}