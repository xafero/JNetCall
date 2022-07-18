using System;
using System.Runtime.InteropServices;

namespace JNetCall.Sharp.Tools
{
    public static class Interop
    {
        public static byte[] ToByteArray(IntPtr ptr, int size = -1)
        {
            if (size == -1)
            {
                const int header = 4;
                var len = BitConverter.ToInt32(ToByteArray(ptr, header));
                size = header + len;
            }
            var array = new byte[size];
            unsafe
            {
                var arrayPtr = ((byte*)ptr)!;
                for (var i = 0; i < array.Length; i++)
                    array[i] = arrayPtr[i];
            }
            return array;
        }

        public static IntPtr ToPointer(byte[] data)
        {
            var pointer = Marshal.AllocHGlobal(data.Length);
            Marshal.Copy(data, 0, pointer, data.Length);
            return pointer;
        }
    }
}