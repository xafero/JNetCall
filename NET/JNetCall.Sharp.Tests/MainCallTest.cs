// ReSharper disable UnusedMember.Global
using JNetCall.Sharp.Client;
using static JNetCall.Sharp.Client.Tools.ServiceEnv;

namespace JNetCall.Sharp.Tests
{
    public sealed class MainCallTest : CallTest
    {
        internal readonly string Path
            = BuildPath(@"..\..\JVM\alien1-java\target\alien1-java.jar");

        protected override T Create<T>()
        {
            return StdIOClient.Create<T>(Path);
        }
    }
}