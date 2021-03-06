using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using Example.API;
using JNetBase.Sharp.Sys;
using static Example.API.IMultiple;
using static Example.API.ITriggering;

namespace Example.Impl
{
    public class CalculatorService : ICalculator, IStringCache, ISimultaneous, ITriggering
    {
        public double Add(double n1, double n2)
        {
            var result = n1 + n2;
            return result;
        }

        public double Subtract(double n1, double n2)
        {
            var result = n1 - n2;
            return result;
        }

        public double Multiply(double n1, double n2)
        {
            var result = n1 * n2;
            return result;
        }

        public double Divide(double n1, double n2)
        {
            var result = n1 / n2;
            return result;
        }

        public string ToSimpleText(byte y, short s, int i, long l, float f,
            double d, bool b, char c, string t, decimal u, Guid g)
        {
            var bld = new StringBuilder();
            bld.Append(" y = " + y);
            bld.Append(", s = " + s);
            bld.Append(", i = " + i);
            bld.Append(", l = " + l);
            bld.Append(", f = " + f);
            bld.Append(", d = " + d);
            bld.Append(", b = " + b);
            bld.Append(", c = " + c);
            bld.Append(", t = " + t);
            bld.Append(", u = " + u);
            bld.Append(", g = " + g);
            return bld.ToString();
        }

        public string ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f,
            double[] d, bool[] b, char[] c, string[] t, decimal[] u, Guid[] g)
        {
            var bld = new StringBuilder();
            bld.Append(" y = " + Arrays.ToString(y));
            bld.Append(", s = " + Arrays.ToString(s));
            bld.Append(", i = " + Arrays.ToString(i));
            bld.Append(", l = " + Arrays.ToString(l));
            bld.Append(", f = " + Arrays.ToString(f));
            bld.Append(", d = " + Arrays.ToString(d));
            bld.Append(", b = " + Arrays.ToString(b));
            bld.Append(", c = " + Arrays.ToString(c));
            bld.Append(", t = " + Arrays.ToString(t));
            bld.Append(", u = " + Arrays.ToString(u));
            bld.Append(", g = " + Arrays.ToString(g));
            return bld.ToString();
        }

        public int GetLineCount(string[] lines)
        {
            return lines.Length;
        }

        public long GetFileSize(string path)
        {
            return path.Length;
        }

        public byte[] AllocateBytes(int size, byte value)
        {
            var array = new byte[size];
            for (var i = 0; i < array.Length; i++)
                array[i] = value;
            return array;
        }

        public ISet<string> GetUnique(IList<string> lines, bool withTrim)
        {
            var set = new SortedSet<string>();
            foreach (var line in lines)
                set.Add(withTrim ? line.Trim() : line);
            return set;
        }

        public IList<string> GetDouble(ISet<string> lines)
        {
            var list = new List<string>();
            foreach (var line in lines)
                list.Add(line);
            foreach (var line in lines)
                list.Add(line);
            return list;
        }

        public IDictionary<string, int> GetSystemVariables(DateTime dts,
            TimeSpan dur, IDictionary<string, int> parent)
        {
            var map = new Dictionary<string, int>(parent);
            map.Add("year", dts.Year);
            map.Add("seconds", (int) dur.TotalSeconds);
            return map;
        }

        public Task<int> GetId()
        {
            return Task.Factory.StartNew(() => new Random().Next(-100, 100));
        }

        private string _currentWord;

        public Task LoadIt(string word)
        {
            _currentWord = word;
            return Task.CompletedTask;
        }

        public Task<string> RemoveIt()
        {
            var res = Task.FromResult(_currentWord);
            _currentWord = null;
            return res;
        }

        public async Task<Tuple<int, long>> RunIt(int waitMs, int idx)
        {
            await Task.Delay(waitMs);
            var current = Environment.CurrentManagedThreadId;
            return Tuple.Create(idx, (long) current);
        }

        public Tuple<int, string> GetTuple2T(int a, string b)
        {
            return Tuple.Create(a, b);
        }

        public (int, string) GetTuple2V(Tuple<int, string> v)
        {
            return ValueTuple.Create(v.Item1, v.Item2);
        }

        public Tuple<int, string, bool> GetTuple3T(int a, string b, bool c)
        {
            return Tuple.Create(a, b, c);
        }

        public (int, string, bool) GetTuple3V(Tuple<int, string, bool> v)
        {
            return ValueTuple.Create(v.Item1, v.Item2, v.Item3);
        }

        public Tuple<string, string[], int, int[]> GetTuple4T(string a, string[] b, int c, int[] d)
        {
            return Tuple.Create(a, b, c, d);
        }

        public (string, string[], int, int[]) GetTuple4V(Tuple<string, string[], int, int[]> v)
        {
            return ValueTuple.Create(v.Item1, v.Item2, v.Item3, v.Item4);
        }

        public Tuple<int, float, long, string, string> GetTuple5T(int a, float b, long c, string d, string e)
        {
            return Tuple.Create(a, b, c, d, e);
        }

        public (int, float, long, string, string) GetTuple5V(Tuple<int, float, long, string, string> v)
        {
            return ValueTuple.Create(v.Item1, v.Item2, v.Item3, v.Item4, v.Item5);
        }

        public WeekDay FindBestDay(int value)
        {
            if (value == (int)WeekDay.Wednesday) return WeekDay.Wednesday;
            if (value == (int)WeekDay.Friday) return WeekDay.Friday;
            throw new ArgumentException(value + " !");
        }

        public Days FindFreeDays()
        {
            return Days.Saturday | Days.Thursday | Days.Sunday;
        }

        public string GetTextOf(WeekDay[] taken, Days days)
        {
            var bld = new StringBuilder();
            bld.Append(Arrays.ToString(taken));
            bld.Append(" | ");
            bld.Append("[" + days + "]");
            return bld.ToString();
        }

        private readonly IDictionary<int, string> _cache = new Dictionary<int, string>();

        public void Set(int key, string value)
        {
            _cache.Add(key, value);
        }

        public string Get(int key)
        {
            var value = _cache[key];
            if (value == null)
                throw new InvalidOperationException(key + " ?!");
            return value;
        }

        public void Delete(int key)
        {
            _cache.Remove(key);
        }

        public int Size => _cache.Count;

        public void Clean() => Dispose();

        public bool EnumWindows(PCallBack callback, int count)
        {
            for (var i = 0; i < count; i++)
                callback(i, i + count + "!");
            return true;
        }

        public void StartPub(int count)
        {
            for (var i = 0; i < count; i++)
                ThresholdReached?.Invoke(this, new ThresholdEventArgs
                {
                    Threshold = i, TimeReached = DateTime.Now
                });
        }

        public event ThresholdHandler ThresholdReached;

        public virtual string Name => "C#";

        public void Dispose()
        {
        }
    }
}
