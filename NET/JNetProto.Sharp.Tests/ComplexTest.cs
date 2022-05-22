// ReSharper disable StringLiteralTypo
using System.IO;
using Xunit;

namespace JNetProto.Sharp.Tests
{
    public class ComplexTest
    {
        [Theory]
        [InlineData("220000001304040A0000000504000000000000000700000000000014400A0774687269667479", 10, 4, 5, "thrifty")]
        [InlineData("1E0000001304040500000005030000000000000007000000000000F03F0A036E6F74", 5, 3, 1, "not")]
        [InlineData("100000001302040A0000000A0774687269667479", 10, null, null, "thrifty")]
        [InlineData("0C000000130204050000000A036E6F74", 5, null, null, "not")]
        public void ShouldWrite(string hex, int number, long? bigNumber,
            double? decimals, string name)
        {
            var s = new ProtoSettings();

            var isErr = bigNumber == null && decimals == null;
            object value = isErr
                ? new Invalid(number, name)
                : new Example(number, bigNumber!.Value, decimals!.Value, name);

            using var writer = CreateWriter(out var mem, s);
            writer.WriteObject(value);
            var actual = BinaryTest.ToHex(mem);
            Assert.Equal(hex, actual);

            using var reader = CreateReader(mem, s);
            object obj = isErr
                ? reader.ReadObject<Invalid>()
                : reader.ReadObject<Example>();
            Assert.Equal(value, obj);
        }

        private static ProtoConvert CreateWriter(out MemoryStream mem, ProtoSettings s)
        {
            return new ProtoConvert(null, mem = new MemoryStream(), s);
        }

        private static ProtoConvert CreateReader(MemoryStream mem, ProtoSettings s)
        {
            return new ProtoConvert(new MemoryStream(mem.ToArray()), null, s);
        }

        public record struct Example(int Number, long BigNumber,
            double Decimals, string Name);

        public record Invalid(int What, string Why);
    }
}