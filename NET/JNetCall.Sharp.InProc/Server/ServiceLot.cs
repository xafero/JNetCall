using System;
using System.IO;
using System.Reflection;
using JNetCall.Sharp.API;
using JNetCall.Sharp.API.Flow;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Server
{
    public sealed class ServiceLot<T> : AbstractHost<T>, ICaller
    {
        private T _instance;
        private MethodInfo[] _methods;

        public ServiceLot(Type serviceClass) : base(serviceClass)
        {
        }

        public void Build()
        {
            _instance = CreateInst();
            _methods = _instance.GetType().GetMethods();
            ServiceLots.Register(this);
        }

        public bool TryCall(byte[] @in, Stream output)
        {
            using var input = new MemoryStream(@in);
            using var proto = new ProtoConvert(input, output, _config);
            var call = proto.ReadObject<MethodCall>();
            HandleCall(_instance, _methods, call, proto);
            output.Flush();
            return true;
        }
    }
}