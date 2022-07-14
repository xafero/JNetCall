// ReSharper disable FunctionNeverReturns
using System;
using System.IO;
using JNetCall.Sharp.API.Flow;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Server
{
    public sealed class ServiceHost<T> : AbstractHost<T>
    {
        public ServiceHost(Type serviceClass) : base(serviceClass)
        {
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
                HandleCall(inst, methods, call, proto);
            }
        }
    }
}