using System;

namespace Example.API
{
    public interface IMultiple : IDisposable
    {
        Tuple<int, string> GetTuple2T(int a, string b);
        (int, string) GetTuple2V(Tuple<int, string> v);

        Tuple<int, string, bool> GetTuple3T(int a, string b, bool c);
        (int, string, bool) GetTuple3V(Tuple<int, string, bool> v);

        Tuple<string, string[], int, int[]> GetTuple4T(string a, string[] b, int c, int[] d);
        (string, string[], int, int[]) GetTuple4V(Tuple<string, string[], int, int[]> v);

        Tuple<int, float, long, string, string> GetTuple5T(int a, float b, long c, string d, string e);
        (int, float, long, string, string) GetTuple5V(Tuple<int, float, long, string, string> v);

        WeekDay FindBestDay(int value);
        Days FindFreeDays();
        string GetTextOf(WeekDay[] taken, Days days);

        enum WeekDay
        {
            Monday = 1,
            Tuesday = 2,
            Wednesday = 3,
            Thursday = 4,
            Friday = 5,
            Saturday = 6,
            Sunday = 7
        }

        [Flags]
        enum Days
        {
            None = 0,
            Sunday = 1,
            Monday = 2,
            Tuesday = 4,
            Wednesday = 8,
            Thursday = 16,
            Friday = 32,
            Saturday = 64
        }
    }
}