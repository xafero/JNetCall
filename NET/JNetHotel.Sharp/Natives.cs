using System;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.API;
using JNetHotel.Sharp.BSD;
using JNetHotel.Sharp.Linux;
using JNetHotel.Sharp.Mac;
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
            if (IsMac)
                return new MacVmRef();
            if (IsBSD)
                return new BsdVmRef();

            throw new InvalidOperationException(RuntimeInformation.OSDescription);
        }

        private static bool IsWindows => RuntimeInformation.IsOSPlatform(OSPlatform.Windows);
        private static bool IsLinux => RuntimeInformation.IsOSPlatform(OSPlatform.Linux);
        private static bool IsMac => RuntimeInformation.IsOSPlatform(OSPlatform.OSX);
        private static bool IsBSD => RuntimeInformation.IsOSPlatform(OSPlatform.FreeBSD);
    }
}