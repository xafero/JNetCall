using System.Collections.Generic;

namespace JNetBase.Sharp
{
    public static class Arrays
    {
        public static string ToString<T>(IEnumerable<T> items)
        {
            return $"[{string.Join(", ", items)}]";
        }
    }
}