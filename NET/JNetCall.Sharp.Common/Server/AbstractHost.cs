using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using JNetCall.Sharp.API;
using JNetProto.Sharp.Beans;
using JNetProto.Sharp.Tools;

namespace JNetCall.Sharp.Server
{
    public abstract class AbstractHost<T> : IDisposable
    {
        private readonly Type _serviceClass;
        private readonly IDictionary<string, Type> _interfaces;
        protected readonly ProtoSettings _config;

        protected AbstractHost(Type serviceClass)
        {
            _serviceClass = serviceClass;
            _interfaces = new Dictionary<string, Type>();
            _config = new ProtoSettings();
        }

        protected T CreateInst()
        {
            var svc = (T)Activator.CreateInstance(_serviceClass);
            return svc;
        }

        public void AddServiceEndpoint<TSvc>()
        {
            AddServiceEndpoint(typeof(TSvc));
        }

        private void AddServiceEndpoint(Type interfaceClass)
        {
            var name = interfaceClass.Name;
            _interfaces.Add(name, interfaceClass);
        }

        protected void HandleCall(object inst, MethodInfo[] methods, 
            MethodCall call, ProtoConvert proto)
        {
            if (!_interfaces.ContainsKey(call.C))
            {
                var debug = call.C;
                Write(proto, debug, MethodStatus.ClassNotFound);
                return;
            }
            var callName = call.M;
            var method = methods.FirstOrDefault(m => CheckMethod(m, callName));
            if (method == null)
            {
                var debug = call.C + "::" + call.M;
                Write(proto, debug, MethodStatus.MethodNotFound);
                return;
            }
            try
            {
                var types = method.GetParameters()
                    .Select(p => p.ParameterType).ToArray();
                var args = Conversions.Convert(types, call.A);
                var res = method.Invoke(inst, args);
                Write(proto, res, MethodStatus.Ok);
            }
            catch (Exception e)
            {
                var cause = e is TargetInvocationException ti
                    ? ti.InnerException
                    : e;
                var debug = cause!.ToString();
                Write(proto, debug, MethodStatus.MethodFailed);
            }
        }

        private const StringComparison Cmp = StringComparison.InvariantCultureIgnoreCase;

        private static bool CheckMethod(MemberInfo m, string callName)
        {
            var mName = m.Name.Replace("_", string.Empty);
            return mName.Equals(callName, Cmp);
        }

        private static void Write(ProtoConvert proto, object res, MethodStatus status)
        {
            var obj = new MethodResult(res, (short)status);
            proto.WriteObject(obj);
            proto.Flush();
        }

        public void Dispose()
        {
            _interfaces.Clear();
        }
    }
}