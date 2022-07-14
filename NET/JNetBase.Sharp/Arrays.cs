using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace JNetBase.Sharp
{
    public static class Arrays
    {
        public static string ToString(IEnumerable items)
        {
            return ToString(items.Cast<object>());
        }

        public static string ToString<T>(IEnumerable<T> items)
        {
            return $"[{string.Join(", ", items)}]";
        }
    }
}