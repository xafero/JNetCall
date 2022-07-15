using System;
using System.Reflection;
using JNetBase.Sharp.Meta;
using JNetCall.Sharp.API.Flow;

namespace JNetCall.Sharp.Impl.Util
{
    public sealed class ClassTools
    {
        private const string AsyncAdd = "_A";

        public static (string, string) ToMethodId(MethodCall call)
        {
            var name = call.M.Replace("_", string.Empty);
            var count = call.A.Length;
            var id = (name + "_" + count).ToLowerInvariant();
            var ida = (id + AsyncAdd).ToLowerInvariant();
            return (ida, id);
        }

        public static string ToMethodId(MethodInfo method)
        {
            var name = method.Name.Replace("_", string.Empty);
            var count = method.GetParameters().Length;
            var suffix = Reflect.IsAsync(method) ? AsyncAdd : string.Empty;
            var id = (name + "_" + count + suffix).ToLowerInvariant();
            return id;
        }

        private const StringComparison Cmp = StringComparison.InvariantCultureIgnoreCase;

        public static bool IsSameMethod(MemberInfo m, string callName)
        {
            var mName = m.Name.Replace("_", string.Empty);
            return mName.Equals(callName, Cmp);
        }

        public static string ToDelegateId(Delegate del)
        {
            var hash = del.Target?.GetHashCode() ?? 0;
            var method = del.Method;
            return hash + "#" + method;
        }
    }
}