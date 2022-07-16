using Example.Impl;
using JNetCall.Sharp.Server;

namespace Example
{
    internal static class Program
    {
        private static void Main()
        {
            using var host = ServiceHosts.Create<CalculatorService>();
            host.RegisterAll();
            host.ServeAndWait();
        }
    }
}