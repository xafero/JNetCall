using Example.API;
using Xunit;

namespace JNetCall.Sharp.Tests
{
    public class CallTest
    {
        internal readonly string Path =
            ServiceEnv.BuildPath(@"..\..\JVM\alien-java\target\alien-java.jar");

        [Fact]
        public void ShouldCache()
        {
            var input = new[] { "life", "on", "mars" };
            using var client = ServiceClient.Create<IStringCache>(Path);

            client.Set(42, input[0]);
            Assert.Equal(-1, client.Size);
            Assert.Equal(input[0], client.Get(42));

            client.Delete(42);
            Assert.Equal(-1, client.Size);

            client.Set(43, input[1]);
            client.Set(44, input[2]);
            Assert.Equal(-1, client.Size);

            Assert.Equal(input[1], client.Get(43));
            Assert.Equal(input[2], client.Get(44));

            client.Delete(43);
            client.Delete(44);
            Assert.Equal(-1, client.Size);
        }
    }
}