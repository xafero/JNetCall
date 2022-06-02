using System.Collections.Generic;
using System.IO;
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
        public static byte[] Call(byte[] input)
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
    }
}