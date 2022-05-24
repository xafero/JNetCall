using System;
using System.Collections.Generic;
using Example.API;
using JNetCall.Sharp.Client;
using Microsoft.VisualStudio.TestPlatform.TestHost;
using Xunit;
using static JNetCall.Sharp.Client.ServiceEnv;

namespace JNetCall.Sharp.Tests
{
    public class CallTest
    {
        internal readonly string Path
            = BuildPath(@"..\..\JVM\alien-java\target\alien-java.jar");

        [Fact]
        public void ShouldCallCache()
        {
            var input = new[] { "life", "on", "mars" };
            using var client = ServiceClient.Create<IStringCache>(Path);

            client.Set(42, input[0]);
            Assert.Equal(1, client.Size);
            Assert.Equal(input[0], client.Get(42));

            client.Delete(42);
            Assert.Equal(0, client.Size);

            client.Set(43, input[1]);
            client.Set(44, input[2]);
            Assert.Equal(2, client.Size);

            Assert.Equal(input[1], client.Get(43));
            Assert.Equal(input[2], client.Get(44));

            client.Delete(43);
            client.Delete(44);
            Assert.Equal(0, client.Size);
        }

        [Fact]
        public void ShouldCallMultiple()
        {
            using var client = ServiceClient.Create<IMultiple>(Path);

            var t2T = client.GetTuple2T(200, "Greece");
            var t2V = client.GetTuple2V(t2T);
            Assert.Equal(t2T, t2V.ToTuple());

            var t3T = client.GetTuple3T(1, "cat", true);
            var t3V = client.GetTuple3V(t3T);
            Assert.Equal(t3T, t3V.ToTuple());

            var t4T = client.GetTuple4T("perl", new[] { "java", "c#" }, 1, new[] { 2, 3 });
            var t4V = client.GetTuple4V(t4T);
            Assert.Equal(t4T, t4V.ToTuple());

            var t5T = client.GetTuple5T(1, 1.5f, 2L, "dot", "net");
            var t5V = client.GetTuple5V(t5T);
            Assert.Equal(t5T, t5V.ToTuple());

            var bd1 = client.FindBestDay(3);
            var bd2 = client.FindBestDay(5);
            var bds = client.FindFreeDays();
            var bdt = client.GetTextOf(new[] { bd1, bd2 }, bds);
            Assert.Equal("?", bdt);
        }

        [Fact]
        public void ShouldCallDataTyped()
        {
            using var client = ServiceClient.Create<IDataTyped>(Path);

            var now = DateTime.Now;
            var dur = TimeSpan.FromSeconds(94);
            var env = new Dictionary<string, int> { { "Caller", nameof(Program).Length } };
            var dict = client.GetSystemVariables(now, dur, env);
            Assert.Equal("?", string.Join("|", dict));

            var lines = new List<string> { "Dog  ", "Hot", "Dog ", "Dog", "Hot    ", "Cat", "Cat", "Hot", "Hot" };
            var lineCount = client.GetLineCount(lines.ToArray());
            var set = client.GetUnique(lines, withTrim: true);
            Assert.Equal(string.Join("|", lines), string.Join("|", set));
            Assert.Equal(-1, lineCount);

            var list = client.GetDouble(set);
            Assert.Equal(string.Join("|", set), string.Join("|", list));

            var fs = client.AllocateBytes(3, 42);
            Assert.Equal("?", string.Join("|", fs));

            var bs = client.GetFileSize(@"Z:\Nothing\Good\No fun with that file.txt");
            Assert.Equal(-1, bs);

            var a = new
            {
                y = byte.MaxValue, s = short.MaxValue, i = int.MaxValue, l = long.MaxValue, f = float.MaxValue,
                d = double.MaxValue, b = true, c = char.MaxValue, t = "Str", u = decimal.MaxValue, g = Guid.NewGuid()
            };
            var txt = client.ToSimpleText(a.y, a.s, a.i, a.l, a.f, a.d, a.b, a.c, a.t, a.u, a.g);
            Assert.Equal("?", txt);

            var b = new
            {
                y = new[] { (byte)42 }, s = new[] { short.MinValue }, i = new[] { int.MinValue }, l = new[] { long.MinValue },
                f = new[] { float.MinValue }, d = new[] { double.MinValue }, b = new[] { false }, c = new[] { 'X' },
                t = new[] { "Str1" }, u = new[] { decimal.MinValue }, g = new[] { Guid.Empty }
            };
            txt = client.ToArrayText(b.y, b.s, b.i, b.l, b.f, b.d, b.b, b.c, b.t, b.u, b.g);
            Assert.Equal("?", txt);
        }

        [Fact]
        public void ShouldCallCalculator()
        {
            using var client = ServiceClient.Create<ICalculator>(Path);
            Assert.Equal("Java", client.Name);

            var value1 = 100.00D;
            var value2 = 15.99D;
            var result = client.Add(value1, value2);
            Assert.Equal(115.99, result);

            value1 = 145.00D;
            value2 = 76.54D;
            result = client.Subtract(value1, value2);
            Assert.Equal(68.46, result);

            value1 = 9.00D;
            value2 = 81.25D;
            result = client.Multiply(value1, value2);
            Assert.Equal(731.25, result);

            value1 = 22.00D;
            value2 = 7.00D;
            result = client.Divide(value1, value2);
            Assert.Equal(3.142857142857143, result);
        }
    }
}