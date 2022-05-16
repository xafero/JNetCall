using System;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using Conv = System.Convert;

namespace JNetCall.Sharp
{
    internal static class Conversions
    {
        public static object[] Convert(Type[] types, object[] values)
        {
            for (var i = 0; i < types.Length && i < values.Length; i++)
            {
                var value = values[i];
                if (value == null)
                    continue;
                var type = types[i];
                if (type.IsInstanceOfType(value))
                    continue;
                values[i] = Convert(type, value);
            }
            return values;
        }

        public static object Convert(Type type, object value)
        {
            if (value == null || type.IsInstanceOfType(value))
                return value;
            if (type == typeof(int))
                return Conv.ChangeType(value, type);
            if (type.IsGenericType)
            {
                var genType = type.GetGenericTypeDefinition();
                if (genType == typeof(ISet<>))
                {
                    var itemType = type.GetGenericArguments()[0];
                    var setType = typeof(SortedSet<>).MakeGenericType(itemType);
                    var set = ((JArray)value).ToObject(setType);
                    return set;
                }
            }
            var debug = $"{type} / {value} / {value.GetType()}";
            throw new ArgumentException(debug);
        }
    }
}