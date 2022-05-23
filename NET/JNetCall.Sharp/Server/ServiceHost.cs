// ReSharper disable FunctionNeverReturns
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using JNetCall.Sharp.API;
using JNetCall.Sharp.Tools;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Server
{
    public sealed class ServiceHost<T> : IDisposable
    {
        private readonly Type _serviceClass;
        private readonly IDictionary<string, Type> _interfaces;
        private readonly ProtoSettings _config;

        public ServiceHost(Type serviceClass)
        {
            _serviceClass = serviceClass;
            _interfaces = new Dictionary<string, Type>();
            _config = new ProtoSettings();
        }

        public void AddServiceEndpoint(Type interfaceClass)
        {
            var name = interfaceClass.Name;
            _interfaces.Add(name, interfaceClass);
        }

        private T CreateInst()
        {
            var svc = (T)Activator.CreateInstance(_serviceClass);
            return svc;
        }

        private static void Write(ProtoConvert proto, object res, MethodStatus status)
        {
            var obj = new MethodResult(res, (short)status);
            proto.WriteObject(obj);
            proto.Flush();
        }

        private static ProtoConvert ReadSync(Stream @in, Stream @out, ProtoSettings cfg)
        {
            var convert = new ProtoConvert(@in, @out, cfg);
            const int marker = 0xEE;
            // Send flag
            @out.WriteByte(marker);
            @out.Flush();
            // Receive flag
            while (@in.ReadByte() != marker)
            {
            }
            // Ready!
            return convert;
        }

        public void Open(Stream input, Stream output)
        {
            var inst = CreateInst();
            var methods = inst.GetType().GetMethods();
            using var proto = ReadSync(input, output, _config);
            while (proto.ReadObject<MethodCall>() is var call)
            {
                if (!_interfaces.ContainsKey(call.C))
                {
                    var debug = call.C;
                    Write(proto, debug, MethodStatus.ClassNotFound);
                    continue;
                }
                var callName = call.M;
                var method = methods.FirstOrDefault(m => CheckMethod(m, callName));
                if (method == null)
                {
                    var debug = call.C + "::" + call.M;
                    Write(proto, debug, MethodStatus.MethodNotFound);
                    continue;
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
        }

        private const StringComparison Cmp = StringComparison.InvariantCultureIgnoreCase;

        private static bool CheckMethod(MemberInfo m, string callName)
        {
            return m.Name.Equals(callName, Cmp);
        }

        public void Dispose()
        {
            _interfaces.Clear();
        }
    }
}