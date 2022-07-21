using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace JNetBase.Sharp.Sys
{
    public static class Arrays
    {
        public static string ToString<T>(IEnumerable<T> items)
        {
            return $"[{string.Join(", ", items)}]";
        }

        public static object[] AsObjectArray(IEnumerable items)
        {
            return items.Cast<object>().ToArray();
        }

        public static Array AsTypedArray(object[] args, Type type, Func<object, Type, object> convert = null)
        {
            convert ??= Convert.ChangeType;
            var array = Array.CreateInstance(type, args.Length);
            for (var i = 0; i < args.Length; i++)
                array.SetValue(convert(args[i], type), i);
            return array;
        }

        private static readonly Type ListType = typeof(List<>);

        public static IList AsTypedArrayList(object[] args, Type type, Func<object, Type, object> convert = null)
        {
            convert ??= Convert.ChangeType;
            var list = (IList)Activator.CreateInstance(ListType.MakeGenericType(type))!;
            foreach (var arg in args)
                list.Add(convert(arg, type));
            return list;
        }
    }
}