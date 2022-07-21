using System;
using System.Collections;
using System.Linq;
using System.Reflection;
using System.Runtime.CompilerServices;
using JNetBase.Sharp.Sys;
using JNetProto.Sharp.API;
using JNetProto.Sharp.Core;

namespace JNetProto.Sharp.Tools
{
    public static class Conversions
    {
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
            var kind = DataTypes.GetKind(type);
            if (kind.Kind != DataType.Unknown)
            {
                if (kind is DataTypes.ArrayDt at && at.Item.Kind == DataType.Unknown)
                {
                    var arrayType = type.GetElementType();
                    var array = Arrays.AsTypedArray(args, arrayType, ConvertRaw);
                    return array;
                }
                if (kind is DataTypes.ListDt lt && lt.Item.Kind == DataType.Unknown)
                {
                    var listType = type.GenericTypeArguments[0];
                    var list = Arrays.AsTypedArrayList(args, listType, ConvertRaw);
                    return list;
                }
            }
            var inputTypes = args.Select(DataTypes.ToType).ToArray();
            var creator = type.GetConstructor(inputTypes);
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
            var outputTypes = creator.GetParameters();
            args = ConvertFor(args, outputTypes);
            return creator.Invoke(args);
        }

        private static object[] ConvertFor(object[] args, ParameterInfo[] parameters)
        {
            for (var i = 0; i < args.Length && i < parameters.Length; i++)
            {
                var prm = parameters[i].ParameterType;
                var arg = args[i];
                var value = ConvertRaw(arg, prm);
                args[i] = value;
            }
            return args;
        }

        private static object ConvertRaw(object v, Type t)
        {
            if (t.IsEnum)
                return v;
            if (t == typeof(object))
                return v;
            if (t.IsInstanceOfType(v))
                return v;
            if (v is object[] va)
                return FromObjectArray(t, va);
            return System.Convert.ChangeType(v, t);
        }

        public static object ToObjectArray(object obj)
        {
            var kind = DataTypes.GetKind(obj);
            if (kind.Kind != DataType.Unknown)
            {
                if (kind is DataTypes.ArrayDt at && at.Item.Kind == DataType.Unknown)
                    obj = Arrays.AsObjectArray((IEnumerable)obj);
                if (kind is DataTypes.ListDt lt && lt.Item.Kind == DataType.Unknown)
                    obj = Arrays.AsObjectArray((IEnumerable)obj);
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