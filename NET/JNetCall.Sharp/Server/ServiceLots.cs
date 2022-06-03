using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using JNetCall.Sharp.API;

namespace JNetCall.Sharp.Server
{
    public static class ServiceLots
    {
        public static ServiceLot<T> Create<T>()
        {
            var instance = new ServiceLot<T>(typeof(T));
            return instance;
        }

        private static readonly IList<ICaller> Lots = new List<ICaller>();

        internal static void Register<T>(ServiceLot<T> lot)
        {
            Lots.Add(lot);
        }

        // ReSharper disable UnusedMember.Global
        private static byte[] Call(byte[] input)
        {
            foreach (var lot in Lots)
            {
                using var output = new MemoryStream();
                if (!lot.TryCall(input, output))
                    continue;
                return output.ToArray();
            }
            return new[] { unchecked((byte)-1) };
        }

        public static IntPtr Call(IntPtr inputPtr)
        {
            var input = ToByteArray(inputPtr);
            var output = Call(input);
            return ToPointer(output);
        }

        private static IntPtr ToPointer(byte[] data)
        {
            var pointer = Marshal.AllocHGlobal(data.Length);
            Marshal.Copy(data, 0, pointer, data.Length);
            return pointer;
        }

        private static byte[] ToByteArray(IntPtr ptr, int size = -1)
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
    }
}