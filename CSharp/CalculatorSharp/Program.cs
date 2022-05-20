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
                y = byte.MaxValue, s = short.MaxValue, i = int.MaxValue, l = long.MaxValue,
                f = float.MaxValue, d = double.MaxValue, b = true, c = char.MaxValue, t = "Str",
                u = decimal.MaxValue, g = Guid.NewGuid()
            };
            var txt = client.ToSimpleText(a.y, a.s, a.i, a.l, a.f, a.d, a.b, a.c, a.t, a.u, a.g);
            Console.WriteLine("SimpleText({0}) = {2}           [{1}]", a, txt, Environment.NewLine);

            var b = new
            {
                y = new[] { (byte)42 }, s = new[] { short.MinValue }, i = new[] { int.MinValue },
                l = new[] { long.MinValue }, f = new[] { float.MinValue }, d = new[] { double.MinValue },
                b = new[] { false }, c = new[] { 'X' }, t = new[] { "Str1" },
                u = new[] { decimal.MinValue }, g = new[] { Guid.Empty }
            };
            txt = client.ToArrayText(b.y, b.s, b.i, b.l, b.f, b.d, b.b, b.c, b.t, b.u, b.g);
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

            var t2T = client.GetTuple2T(200, "Greece");
            var t2V = client.GetTuple2V(t2T);
            Console.WriteLine("GetTuple2() = {0} {1}", t2T, t2V);

            var t3T = client.GetTuple3T(1, "cat", true);
            var t3V = client.GetTuple3V(t3T);
            Console.WriteLine("GetTuple3() = {0} {1}", t3T, t3V);

            var t4T = client.GetTuple4T("perl", new[] { "java", "c#" }, 1, new[] { 2, 3 });
            var t4V = client.GetTuple4V(t4T);
            Console.WriteLine("GetTuple4() = {0} {1}", t4T, t4V);

            var t5T = client.GetTuple5T(1, 1.5f, 2L, "dot", "net");
            var t5V = client.GetTuple5V(t5T);
            Console.WriteLine("GetTuple5() = {0} {1}", t5T, t5V);

            var bd1 = client.FindBestDay(3);
            var bd2 = client.FindBestDay(5);
            var bds = client.FindFreeDays();
            var bdt = client.GetTextOf(new[] { bd1, bd2 }, bds);
            Console.WriteLine("GetTextOf() = {0}", bdt);

            Console.WriteLine("\nPress <Enter> to terminate the client.");
            Console.ReadLine();
            client.Dispose();
        }
    }
}