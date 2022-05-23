using System.Collections.Generic;

namespace JNetProto.Sharp.Compat
{
    public static class Arrays
    {
        public static string ToString<T>(IEnumerable<T> items)
        {
            return $"[{string.Join(", ", items)}]";
        }
    }
}