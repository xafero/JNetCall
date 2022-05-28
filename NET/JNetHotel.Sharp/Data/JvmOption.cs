using System;
using System.Runtime.InteropServices;

namespace JNetHotel.Sharp.Data
{
    [StructLayout(LayoutKind.Sequential, Pack = 0)]
    public struct JvmOption
    {
        public IntPtr optionString;
        private readonly IntPtr extraInfo;
    }
}