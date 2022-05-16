using System;
using Example.API;
using JNetCall.Sharp;
using Newtonsoft.Json;
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

            var a = new
            {
                y = byte.MaxValue, s = short.MaxValue, i = int.MaxValue, l = long.MaxValue,
                f = float.MaxValue, d = double.MaxValue, b = true, c = char.MaxValue, t = "Str"
            };
            var txt = client.ToSimpleText(a.y, a.s, a.i, a.l, a.f, a.d, a.b, a.c, a.t);
            Console.WriteLine("SimpleText({0}) = [{1}]", a, txt);

            var b = new
            {
                y = new[] { byte.MinValue }, s = new[] { short.MinValue }, i = new[] { int.MinValue },
                l = new[] { long.MinValue }, f = new[] { float.MinValue }, d = new[] { double.MinValue },
                b = new[] { false }, c = new[] { char.MinValue }, t = new[] { "Str1" }
            };
            txt = client.ToArrayText(b.y, b.s, b.i, b.l, b.f, b.d, b.b, b.c, b.t);
            Console.WriteLine("ArrayText({0}) = [{1}]", JsonConvert.SerializeObject(b), txt);

            Console.WriteLine("\nPress <Enter> to terminate the client.");
            Console.ReadLine();
            client.Dispose();
        }
    }
}