using System;
using System.Collections.Generic;
using JNetHotel.Sharp;

namespace JNetCall.Sharp.Client
{
    public sealed class JvmContainer : IDisposable
    {
        private readonly Jvm _vm;

        public JvmContainer(string jar)
        {
            _vm = SetupJvm(jar);
        }

        private static Jvm SetupJvm(string jar)
        {
            var vmRef = Natives.GetVmRef();
            vmRef.LoadLib();
            var jvm = new Jvm(vmRef, jar);
            InstallStop(jvm);
            return jvm;
        }

        private static void InstallStop(IDisposable vm)
        {
            var domain = AppDomain.CurrentDomain;
            domain.ProcessExit += (_, _) => vm.Dispose();
        }

        public byte[] SendAndGetArray(byte[] input)
        {
            var args = new List<object> { input };
            const string type = "Lx/Boot;";
            var output = _vm.CallStaticMethod<byte[]>(type, "call", "([B)[B", args);
            return output;
        }

        public void Dispose()
        {
        }
    }
}