using System;
using Example.API;
using JNetCall.Sharp;
using static JNetCall.Sharp.ServiceEnv;

namespace Example
{
    internal static class Program
    {
        private static void Main()
        {
            const string path = @"..\..\Java\CalculatorJava\target\calculator-java.jar";
            var client = ServiceClient.Create<ICalculator>(BuildPath(path));

            var value1 = 100.00D;
            var value2 = 15.99D;
            var result = client.Add(value1, value2);
            Console.WriteLine("Add({0} {1}) = {2}", value1, value2, result);

            value1 = 145.00D;
            value2 = 76.54D;
            result = client.Subtract(value1, value2);
            Console.WriteLine("Subtract({0} {1}) = {2}", value1, value2, result);

            value1 = 9.00D;
            value2 = 81.25D;
            result = client.Multiply(value1, value2);
            Console.WriteLine("Multiply({0} {1}) = {2}", value1, value2, result);

            value1 = 22.00D;
            value2 = 7.00D;
            result = client.Divide(value1, value2);
            Console.WriteLine("Divide({0} {1}) = {2}", value1, value2, result);

            Console.WriteLine("\nPress <Enter> to terminate the client.");
            Console.ReadLine();
            client.Dispose();
        }
    }
}