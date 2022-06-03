// ReSharper disable UnusedMember.Global
// ReSharper disable CheckNamespace
using System;
using Example.API;
using Example.Impl;
using JNetCall.Sharp.Server;

namespace X
{
    public static class Boot
    {
        static Boot()
        {
            var host = ServiceLots.Create<CalculatorService>();

            host.AddServiceEndpoint<ICalculator>();
            host.AddServiceEndpoint<IDataTyped>();
            host.AddServiceEndpoint<IMultiple>();
            host.AddServiceEndpoint<IStringCache>();

            host.Build();
        }

        public static IntPtr Call(IntPtr input) => ServiceLots.Call(input);
    }
}