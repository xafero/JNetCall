using System;
using System.Collections.Generic;
using System.IO;
using Castle.DynamicProxy;
using JNetCall.Sharp.API.Flow;
using JNetHotel.Sharp;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Client
{
    internal sealed class JvmInterceptor : AbstractInterceptor
    {
        public JvmInterceptor(string jar) : base(jar)
        {
        }

        private static readonly object Sync = new();
        private static Jvm _vm;

        private static Jvm SetupJvm(string jar)
        {
            lock (Sync)
            {
                if (_vm != null)
                    return _vm;

                var vmRef = Natives.GetVmRef();
                vmRef.LoadLib();
                var jvm = new Jvm(vmRef, jar);
                return jvm;
            }
        }

        protected override void Prepare()
        {
            lock (Sync)
            {
                _vm = SetupJvm(Jar);
            }
        }

        protected override void Start()
        {
            InitBoot();
        }

        private static void InitBoot()
        {
            _vm.CallStaticVoidMethod("LBoot;", "Init", "()V", new List<object>());
        }

        private static byte[] SendAndGetArray(byte[] input)
        {
            var args = new List<object> { input };
            const string type = "Ljnetcall/java/server/ServiceLots;";
            var output = _vm.CallStaticMethod<byte[]>(type, "call", "([B)[B", args);
            return output;
        }

        protected override void Stop(int milliseconds = 250)
        {
            var domain = AppDomain.CurrentDomain;
            domain.ProcessExit += (_, _) => _vm.Dispose();
        }

        public override void Intercept(IInvocation invocation)
        {
            using var input = new MemoryStream();
            using var output = new MemoryStream();
            using var proto = new ProtoConvert(input, output, Settings);
            var call = Pack(invocation);
            if (call == null)
                return;
            Write(proto, call);
            var array = output.ToArray();
            var result = SendAndGetArray(array);
            input.Write(result);
            input.Position = 0L;
            var answer = Read<MethodResult>(proto);
            Unpack(invocation, answer);
        }

        protected override string GetErrorDetails()
        {
            return string.Empty;
        }
    }
}