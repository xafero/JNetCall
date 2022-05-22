using System;
using System.Collections.Generic;
using System.Linq;
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
            if (type == typeof(byte[]))
                return ((JArray)value).ToObject<byte[]>();
            if (type == typeof(int[]))
                return ((JArray)value).ToObject<int[]>();
            if (type == typeof(string[]))
                return ((JArray)value).ToObject<string[]>();
            if (type.IsGenericType)
            {
                var genType = type.GetGenericTypeDefinition();
                if (genType == typeof(Tuple<,>) || genType == typeof(ValueTuple<,>) ||
                    genType == typeof(Tuple<,,>) || genType == typeof(ValueTuple<,,>) ||
                    genType == typeof(Tuple<,,,>))
                {
                    var rawArgs = ((JObject)value)["valueArray"]!.Values<JToken>();
                    var convArgs = type.GetGenericArguments()
                        .Zip(rawArgs.Select(z => (z as JValue)?.Value ?? z))
                        .Select(p => Convert(p.First, p.Second)).ToArray();
                    var resTuple = Activator.CreateInstance(type, convArgs);
                    return resTuple;
                }
                if (genType == typeof(ISet<>))
                {
                    var itemType = type.GetGenericArguments()[0];
                    var setType = typeof(SortedSet<>).MakeGenericType(itemType);
                    return ((JArray)value).ToObject(setType);
                }
                if (genType == typeof(IList<>))
                {
                    var itemType = type.GetGenericArguments()[0];
                    var listType = typeof(List<>).MakeGenericType(itemType);
                    return ((JArray)value).ToObject(listType);
                }
                if (genType == typeof(IDictionary<,>))
                {
                    var dictTypes = type.GetGenericArguments();
                    var keyType = dictTypes[0];
                    var valType = dictTypes[1];
                    var dictType = typeof(Dictionary<,>).MakeGenericType(keyType, valType);
                    return ((JObject)value).ToObject(dictType);
                }
            }
            var debug = $"{type} / {value} / {value.GetType()}";
            throw new ArgumentException(debug);
        }

        private static readonly string[] Nonsense = GetAssemblyStr(typeof(string));

        private static string[] GetAssemblyStr(params Type[] types)
            => types.Select(t => t.AssemblyQualifiedName!.Split(", ", 2)[1])
                .OrderBy(t => t).Distinct().ToArray();

        public static string GetHint(object arg)
        {
            var typeName = arg.GetType().FullName!;
            return GetHintType(typeName);
        }

        private static string GetHintType(string typeName)
        {
            string tmp;
            if (typeName.EndsWith(tmp = "[]"))
                return $"{GetHintType(typeName[..^2])}{tmp}";
            typeName = Simplify(Nonsense, typeName);
            if (typeName.Contains(tmp = "List`1[") ||
                typeName.Contains(tmp = "Set`1["))
            {
                var item = typeName.Split(tmp, 2)[1].Trim('[', ']');
                return GetHintType($"{item}[]");
            }
            if (typeName.Contains(tmp = "Dictionary`2["))
            {
                var items = SplitGenericArgs(typeName, tmp);
                return $"map<{GetHintType(items[0])};{GetHintType(items[1])}>";
            }
            if (typeName.Contains(tmp = "Tuple`2[") || typeName.Contains(tmp = "Tuple`3[") ||
                typeName.Contains(tmp = "Tuple`4["))
            {
                var items = SplitGenericArgs(typeName, tmp);
                return $"tuple<{string.Join(";", items.Select(GetHintType))}>";
            }
            switch (typeName)
            {
                case "System.Boolean": return "bool";
                case "System.Char": return "char";
                case "System.Byte": return "int8";
                case "System.Int16": return "int16";
                case "System.Int32": return "int32";
                case "System.Int64": return "int64";
                case "System.Single": return "float32";
                case "System.Double": return "float64";
                case "System.Decimal": return "float128";
                case "System.String": return "string";
                case "System.Guid": return "guid";
                case "System.DateTime": return "datetime";
                case "System.TimeSpan": return "timespan";
                default: throw new InvalidOperationException(typeName);
            }
        }

        private static string[] SplitGenericArgs(string typeName, string tmp)
        {
            return typeName.Split(tmp, 2)[1].Trim('[', ']').Split("],[");
        }

        private static string Simplify(IEnumerable<string> remove, string typeName)
        {
            foreach (var part in remove)
                typeName = typeName.Replace($", {part}", string.Empty);
            return typeName;
        }
    }
}