using System;
using JNetHotel.Sharp.Data;

namespace JNetHotel.Sharp.API
{
    public interface IVmRef
    {
        unsafe JniResult CreateVm(out IntPtr pvm, out IntPtr pEnv, JvmInitArgs* args);

        JniResult GetCreatedVms(out IntPtr pvm, int size, out int size2);

        unsafe JniResult GetDefaultArgs(JvmInitArgs* args);

        string VmDll { get; }

        void LoadLib();
    }
}