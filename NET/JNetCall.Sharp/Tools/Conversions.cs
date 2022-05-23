using System;

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
            // TODO !
            return arg;
        }
    }
}