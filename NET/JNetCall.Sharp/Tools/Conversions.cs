using System;
using System.Linq;
using System.Runtime.CompilerServices;

namespace JNetCall.Sharp.Tools
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
            // TODO !
            return arg;
        }
    }
}