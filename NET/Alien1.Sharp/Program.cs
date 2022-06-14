using System;
using Example.API;
using Example.Impl;
using JNetCall.Sharp.Server;

namespace Example
{
    internal static class Program
    {
        private static void Main()
        {
            using (var host = ServiceHosts.Create<CalculatorService>())
            {
                host.AddServiceEndpoint<ICalculator>();
                host.AddServiceEndpoint<IDataTyped>();
                host.AddServiceEndpoint<IMultiple>();
                host.AddServiceEndpoint<IStringCache>();
                host.AddServiceEndpoint<ISimultaneous>();

                var @in = Console.OpenStandardInput();
                var @out = Console.OpenStandardOutput();
                host.Open(@in, @out);
            }
        }
    }
}