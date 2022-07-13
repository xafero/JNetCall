using System;
using System.Linq;
using System.Reflection;
using System.Runtime.CompilerServices;
using JNetProto.Sharp.API;
using JNetProto.Sharp.Core;

namespace JNetProto.Sharp.Tools
{
    public static class Conversions
    {
        public static object[] Convert(Type[] types, object[] args)
        {
            for (var i = 0; i < args.Length; i++)
                args[i] = Convert(types[i], args[i]);
            return args;
        }

        public static object Convert(Type type, object arg)
        {
            if (type.IsInstanceOfType(arg))
            {
                return arg;
            }
            if (arg is ITuple tuple)
            {
                var values = new object[tuple.Length];
                for (var i = 0; i < values.Length; i++)
                    values[i] = tuple[i];
                var conv = Activator.CreateInstance(type, values);
                return conv;
            }
            return arg;
        }

        private static bool IsUsable(ConstructorInfo creator, object[] args)
        {
            return creator.GetParameters().Length == args.Length;
        }

        public static object FromObjectArray(Type type, object[] args)
        {
            var cTypes = args.Select(DataTypes.ToType).ToArray();
            var creator = type.GetConstructor(cTypes);
            if (creator == null || !IsUsable(creator, args))
            {
                creator = type.GetConstructors().FirstOrDefault();
                if (creator == null || !IsUsable(creator, args))
                {
                    var props = type.GetProperties();
                    if (props.Length == args.Length)
                    {
                        var obj = Activator.CreateInstance(type);
                        for (var i = 0; i < props.Length; i++)
                        {
                            var arg = args[i];
                            var prop = props[i];
                            prop.SetValue(obj, arg);
                        }
                        return obj;
                    }
                    throw new ArgumentException($"No constructor: {type}");
                }
            }
            return creator.Invoke(args);
        }

        public static object ToObjectArray(object obj)
        {
            var kind = DataTypes.GetKind(obj);
            if (kind.Kind != DataType.Unknown)
            {
                if (obj is object[] ar && ar.Any(a => DataTypes.GetKind(a).Kind == default))
                    for (var i = 0; i < ar.Length; i++)
                        ar[i] = ToObjectArray(ar[i]);
                return obj;
            }
            var type = obj.GetType();
            var props = type.GetProperties();
            var args = new object[props.Length];
            for (var i = 0; i < args.Length; i++)
                args[i] = ToObjectArray(props[i].GetValue(obj));
            return args;
        }
    }
}
