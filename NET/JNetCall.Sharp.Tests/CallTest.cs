using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Example.API;
using JNetBase.Sharp.Sys;
using Microsoft.VisualStudio.TestPlatform.TestHost;
using Xunit;
using static Example.API.ITriggering;

namespace JNetCall.Sharp.Tests
{
    public abstract class CallTest
    {
        protected abstract T Create<T>() where T : class, IDisposable;

        protected virtual string Patch(string input) => input;

        protected virtual int MaxListWait => 1;

        [Fact]
        public void ShouldCallCache()
        {
            var input = new[] { "life", "on", "mars" };
            using var client = Create<IStringCache>();

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

            Assert.Throws<InvalidOperationException>(() => client.Get(89));
            client.Clean();
        }

        [Fact]
        public void ShouldCallMultiple()
        {
            using var client = Create<IMultiple>();

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
            Assert.Equal("Wednesday", bd1.ToString());
            var bd2 = client.FindBestDay(5);
            Assert.Equal("Friday", bd2.ToString());
            var bds = client.FindFreeDays();
            Assert.Equal("Sunday, Thursday, Saturday", bds.ToString());

            var bdt = client.GetTextOf(new[] { bd1, bd2 }, bds);
            Assert.Equal("[Wednesday, Friday] | [Sunday, Thursday, Saturday]", bdt);
        }

        [Fact]
        public void ShouldCallDataTyped()
        {
            using var client = Create<IDataTyped>();

            var now = DateTime.Now;
            var dur = TimeSpan.FromSeconds(94);
            var env = new Dictionary<string, int> { { "Caller", nameof(Program).Length } };
            var dict = client.GetSystemVariables(now, dur, env);
            Assert.Equal("[Caller, 7]|[seconds, 94]|[year, 2023]", string.Join("|", dict));

            var lines = new List<string> { "Dog  ", "Hot", "Dog ", "Dog", "Hot    ", "Cat", "Cat", "Hot", "Hot" };
            var lineCount = client.GetLineCount(lines.ToArray());
            var set = client.GetUnique(lines, withTrim: true);
            Assert.Equal("Cat|Dog|Hot", string.Join("|", set));
            Assert.Equal(9, lineCount);

            var list = client.GetDouble(set);
            Assert.Equal("Cat|Dog|Hot|Cat|Dog|Hot", string.Join("|", list));

            var fs = client.AllocateBytes(3, 42);
            Assert.Equal("42|42|42", string.Join("|", fs));

            var bs = client.GetFileSize(@"Z:\Nothing\Good\No fun with that file.txt");
            Assert.Equal(41, bs);

            var a = new
            {
                y = byte.MaxValue, s = short.MaxValue, i = int.MaxValue, l = long.MaxValue, f = float.MaxValue,
                d = double.MaxValue, b = true, c = char.MaxValue, t = "Str", u = decimal.MaxValue, 
                g = Guid.Parse("27edb110-afef-4ce3-b8c1-3fcb2ec3fabe")
            };
            var txt = client.ToSimpleText(a.y, a.s, a.i, a.l, a.f, a.d, a.b, a.c, a.t, a.u, a.g).Replace(" ", "").Trim();
            txt = Patch(txt);
            Assert.Equal("y=-1,s=32767,i=2147483647,l=9223372036854775807,f=3.4028235E38,d=1.7976931348623157E308,b=true,c=￿,t=Str,u=79228162514264337593543950335,g=27edb110-afef-4ce3-b8c1-3fcb2ec3fabe", txt);

            var b = new
            {
                y = new[] { (byte)42 }, s = new[] { short.MinValue }, i = new[] { int.MinValue }, l = new[] { long.MinValue },
                f = new[] { float.MinValue }, d = new[] { double.MinValue }, b = new[] { false }, c = new[] { 'X' },
                t = new[] { "Str1" }, u = new[] { decimal.MinValue }, g = new[] { Guid.Empty }
            };
            txt = client.ToArrayText(b.y, b.s, b.i, b.l, b.f, b.d, b.b, b.c, b.t, b.u, b.g).Replace(" ", "").Trim();
            txt = Patch(txt);
            Assert.Equal("y=[42],s=[-32768],i=[-2147483648],l=[-9223372036854775808],f=[-3.4028235E38],d=[-1.7976931348623157E308],b=[false],c=[X],t=[Str1],u=[-79228162514264337593543950335],g=[00000000-0000-0000-0000-000000000000]", txt);
        }

        [Fact]
        public async Task ShouldCallSimultan()
        {
            using var client = Create<ISimultaneous>();

            await client.LoadIt("Hello");

            var id = await client.GetId();
            Assert.True(id is >= -100 and <= 100, id + " ?!");

            var txt = await client.RemoveIt();
            Assert.Equal("Hello", txt);

            const int count = 3;
            var range = Enumerable.Range(0, count);

            var watch = Stopwatch.StartNew();
            var tasks = range.Select(i => client.RunIt(26, i))
                .ToList();
            watch.Stop();
            var listTime = watch.ElapsedMilliseconds;

            var all = Task.WhenAll(tasks).GetAwaiter().GetResult();
            Assert.Equal(count, all.Length);

            var numbers = all.Select(t => t.Item1).Distinct().ToArray();
            Assert.Equal(count, numbers.Length);

            Assert.True(listTime >= 0 && listTime <= MaxListWait, listTime + " ?!");
        }

        [Fact]
        public void ShouldCallCalculator()
        {
            using var client = Create<ICalculator>();
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

        [Fact]
        public void ShouldCallTrigger()
        {
            using var client = Create<ITriggering>();

            const int cbCount = 3;
            var cbList = new List<Tuple<int, string>>();
            var clc = new CountdownEvent(cbCount);

            bool EnumWindowsCallback(int handle, string lParam)
            {
                cbList.Add(Tuple.Create(handle, lParam));
                clc.Signal();
                return true;
            }

            var callOk = client.EnumWindows(EnumWindowsCallback, cbCount);
            Assert.True(callOk);
            clc.Wait(TimeSpan.FromSeconds(5));

            Assert.Equal(cbCount, cbList.Count);
            Assert.Equal("[(0, 3!), (1, 4!), (2, 5!)]", Arrays.ToString(cbList));

            const int evtCount = 4;
            var evtList = new List<Tuple<string, int>>();
            var cle = new CountdownEvent(evtCount);

            void OnThresholdReached(object sender, ThresholdEventArgs e)
            {
                var s = sender.ToString();
                evtList.Add(Tuple.Create(s, e.Threshold));
                cle.Signal();
            }

            client.ThresholdReached += OnThresholdReached;
            client.StartPub(evtCount);
            cle.Wait(TimeSpan.FromSeconds(5));

            Assert.Equal(evtCount, evtList.Count);
            client.ThresholdReached -= OnThresholdReached;
        }
    }
}
