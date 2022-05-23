// ReSharper disable StringLiteralTypo
using System.IO;
using Newtonsoft.Json;
using Xunit;

namespace JNetProto.Sharp.Tests
{
    public class ComplexTest
    {
        [Theory]
        [InlineData("1400000013020A0C007468697320697320676F6F64030800", 3, null, 5, "this is good")]
        [InlineData("0C00000013020A040063726170030600", 2, null, 4, "crap")]
        [InlineData("2600000013040A03004333330A03005365741302042A0000000A01006C0E0A0102000000010069010073", 42, 33, null, "Set")]
        [InlineData("2500000013040A020043370A0300476574130204030000000A01006C0E0A0102000000010069010073", 3, 7, null, "Get")]
        [InlineData("230000001304040A0000000504000000000000000700000000000014400A070074687269667479", 10, 4, 5, "thrifty")]
        [InlineData("1F0000001304040500000005030000000000000007000000000000F03F0A03006E6F74", 5, 3, 1, "not")]
        [InlineData("110000001302040A0000000A070074687269667479", 10, null, null, "thrifty")]
        [InlineData("0D000000130204050000000A03006E6F74", 5, null, null, "not")]
        public void ShouldWrite(string hex, int number, long? bigNumber,
            double? decimals, string name)
        {
            var s = new ProtoSettings();

            var isErr = bigNumber == null && decimals == null;
            var isCall = bigNumber != null && decimals == null;
            var isRes = bigNumber == null && decimals != null;
            object value = isCall ? new Call("C" + bigNumber, name,
                    new object[] { number, "l" }, new[] { "i", "s" })
                : isErr ? new Invalid(number, name)
                : isRes ? new Result(name, (short)(decimals + number))
                : new Example(number, bigNumber!.Value, decimals!.Value, name);

            using var writer = CreateWriter(out var mem, s);
            writer.WriteObject(value);
            var actual = BinaryTest.ToHex(mem);
            Assert.Equal(hex, actual);

            using var reader = CreateReader(mem, s);
            object obj = isCall ? reader.ReadObject<Call>()
                : isErr ? reader.ReadObject<Invalid>()
                : isRes ? reader.ReadObject<Result>()
                : reader.ReadObject<Example>();

            if (obj is not Call)
            {
                Assert.Equal(value, obj);
                return;
            }
            var valueJson = JsonConvert.SerializeObject(value);
            var objJson = JsonConvert.SerializeObject(obj);
            Assert.Equal(valueJson, objJson);
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

        public readonly record struct Call(string C, string M, object[] A, string[] H);
        public readonly record struct Result(object R, short S);
    }
}