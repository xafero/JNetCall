// ReSharper disable UnusedType.Global
// ReSharper disable UnusedMember.Global
// ReSharper disable CheckNamespace

using System;
using Example.Impl;
using JNetCall.Sharp.Server;

namespace X
{
    public static class Boot
    {
        static Boot()
        {
            var host = ServiceLots.Create<CalculatorService>();
            host.RegisterAll();
            host.Serve();
        }

        public static IntPtr Call(IntPtr input) => ServiceLots.Call(input);
    }
}