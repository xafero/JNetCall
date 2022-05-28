// ReSharper disable InconsistentNaming

using System.Runtime.InteropServices;

namespace JNetHotel.Sharp.Interop
{
    [StructLayout(LayoutKind.Sequential, Size = 4)]
    public struct JNIInvokeInterfacePtr
    {
        public readonly unsafe JNIInvokeInterface* functions;
    }
}