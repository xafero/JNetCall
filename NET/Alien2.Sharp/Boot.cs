// ReSharper disable UnusedType.Global
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
            
            // TODO ?!
            // host.registerAll();
            // host.serveAndWait();
            
            host.AddServiceEndpoint<ICalculator>();
            host.AddServiceEndpoint<IDataTyped>();
            host.AddServiceEndpoint<IMultiple>();
            host.AddServiceEndpoint<IStringCache>();
            host.AddServiceEndpoint<ISimultaneous>();

            host.Build();
        }

        public static IntPtr Call(IntPtr input) => ServiceLots.Call(input);
    }
}