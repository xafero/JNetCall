using System.Runtime.InteropServices;

namespace JNetHotel.Sharp.Data
{
    [StructLayout(LayoutKind.Sequential, Pack = 0)]
    public unsafe struct JvmInitArgs
    {
        public int version;
        public int nOptions;
        public JvmOption* options;
        public byte ignoreUnrecognized;
    }
}