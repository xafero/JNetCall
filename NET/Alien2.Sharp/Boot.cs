// ReSharper disable UnusedMember.Global
// ReSharper disable CheckNamespace
using Example.API;
using Example.Impl;
using JNetCall.Sharp.Server;

namespace X
{
    public static class Boot
    {
        public static void Init()
        {
            var host = ServiceLots.Create<CalculatorService>();

            host.AddServiceEndpoint<ICalculator>();
            host.AddServiceEndpoint<IDataTyped>();
            host.AddServiceEndpoint<IMultiple>();
            host.AddServiceEndpoint<IStringCache>();

            host.Build();
        }
    }
}