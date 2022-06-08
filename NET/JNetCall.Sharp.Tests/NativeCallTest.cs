// ReSharper disable UnusedMember.Global
using JNetCall.Sharp.Client;
using static JNetCall.Sharp.Client.ServiceEnv;

namespace JNetCall.Sharp.Tests
{
    public sealed class NativeCallTest : CallTest
    {
        internal readonly string Path
            = BuildPath(@"..\..\JVM\alien2-java\target\alien2-java.jar");

        protected override T Create<T>()
        {
            return InProcClient.Create<T>(Path);
        }
    }
}