using System;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.API;
using JNetHotel.Sharp.Linux;
using JNetHotel.Sharp.Windows;

namespace JNetHotel.Sharp
{
    public static class Natives
    {
        public static IVmRef GetVmRef()
        {
            if (IsLinux)
                return new LinuxVmRef();
            if (IsWindows)
                return new WinVmRef();

            throw new InvalidOperationException(RuntimeInformation.OSDescription);
        }

        private static bool IsWindows => RuntimeInformation.IsOSPlatform(OSPlatform.Windows);
        private static bool IsLinux => RuntimeInformation.IsOSPlatform(OSPlatform.Linux);
    }
}