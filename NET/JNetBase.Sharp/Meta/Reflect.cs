using System;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace JNetBase.Sharp.Meta
{
    public static class Reflect
    {
        public static bool IsAsync(MethodInfo method)
        {
            return IsAsync(method.ReturnType);
        }

        public static bool IsAsync(Type ret)
        {
            return ret.IsAssignableTo(typeof(Task)) || ret.IsAssignableTo(typeof(ValueTask));
        }

        public static bool IsDelegate(Type ret)
        {
            return ret.IsAssignableTo(typeof(Delegate));
        }

        public static Type GetTaskType(Type taskType, Type defaultArg = null)
        {
            var taskArg = taskType.GetGenericArguments().FirstOrDefault();
            if (taskArg == null)
            {
                return defaultArg ?? typeof(object);
            }
            return taskArg;
        }

        public static MethodInfo GetMethod(Func<object, object[], object> func)
        {
            var dest = func.Target!;
            var type = dest.GetType();
            var field = type.GetField("method")!;
            var raw = field.GetValue(func.Target);
            var method = (MethodInfo)raw;
            return method;
        }
    }
}