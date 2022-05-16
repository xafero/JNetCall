using System;
using System.Collections.Generic;
using Example.API;
using JNetCall.Sharp;
using static JNetCall.Sharp.ServiceEnv;
using static Newtonsoft.Json.JsonConvert;

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
                y = sbyte.MaxValue, s = short.MaxValue, i = int.MaxValue, l = long.MaxValue,
                f = float.MaxValue, d = double.MaxValue, b = true, c = char.MaxValue, t = "Str"
            };
            var txt = client.ToSimpleText(a.y, a.s, a.i, a.l, a.f, a.d, a.b, a.c, a.t);
            Console.WriteLine("SimpleText({0}) = {2}           [{1}]", a, txt, Environment.NewLine);

            var b = new
            {
                y = new[] { (byte)42 }, s = new[] { short.MinValue }, i = new[] { int.MinValue },
                l = new[] { long.MinValue }, f = new[] { float.MinValue }, d = new[] { double.MinValue },
                b = new[] { false }, c = new[] { 'X' }, t = new[] { "Str1" }
            };
            txt = client.ToArrayText(b.y, b.s, b.i, b.l, b.f, b.d, b.b, b.c, b.t);
            Console.WriteLine("ArrayText({0}) = {2}          [{1}]", SerializeObject(b), txt, Environment.NewLine);

            var lines = new List<string> { "Dog  ", "Hot", "Dog ", "Dog", "Hot    ", "Cat", "Cat", "Hot", "Hot" };
            var lineCount = client.GetLineCount(lines.ToArray());
            var set = client.GetUnique(lines, withTrim: true);
            Console.WriteLine("Unique({0}) = {2}, {1}", string.Join("|", lines), string.Join("|", set), lineCount);

            var list = client.GetDouble(set);
            Console.WriteLine("Double({0}) = {1}", string.Join("|", set), string.Join("|", list));

            var fs = client.AllocateBytes(3, 42);
            Console.WriteLine("Allocate() = {0}", string.Join("|", fs));

            var bs = client.GetFileSize(@"Z:\Nothing\Good\No fun with that file.txt");
            Console.WriteLine("FileSize() = {0}", bs);

            var now = DateTime.Now;
            var dur = TimeSpan.FromSeconds(94);
            var env = new Dictionary<string, int> { { "Caller", nameof(Program).Length } };
            var dict = client.GetSystemVariables(now, dur, env);
            Console.WriteLine("SystemVars({0} {1}) = {2}", now, dur, string.Join("|", dict));

            Console.WriteLine("\nPress <Enter> to terminate the client.");
            Console.ReadLine();
            client.Dispose();
        }
    }
}