// ReSharper disable StringLiteralTypo

using System;
using System.Collections.Generic;
using System.IO;
using JNetProto.Sharp.Beans;
using Newtonsoft.Json;
using Xunit;
using static JNetProto.Sharp.Tests.BinaryTest;

namespace JNetProto.Sharp.Tests
{
    public class ComplexTest
    {
        [Theory]
        [InlineData("380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'C')]
        [InlineData("380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'N')]
        [InlineData("360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'B')]
        [InlineData("360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'M')]
        [InlineData("34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'A')]
        [InlineData("34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'L')]
        [InlineData("020000001300", 'D')]
        [InlineData("020000001300", 'O')]
        public void ShouldColl(string hex, char mode)
        {
            var isArray0 = mode == 'A';
            var isList1 = mode == 'M';
            var isArray1 = mode == 'B';
            var isList2 = mode == 'N';
            var isArray2 = mode == 'C';
            var isListE = mode == 'O';
            var isArrayE = mode == 'D';

            var example1 = new Call("c", "m", new object[] { 'a' }, new[] { "h" });
            var example2 = new Call("d", "n", new object[] { 'b' }, new[] { "i" });

            object value = isArrayE ? Array.Empty<Call>()
                : isListE ? new List<Call>()
                : isArray2 ? new CallArrayBag2(new[] { example1, example2 }, 0x07)
                : isList2 ? new CallListBag2(new List<Call> { example1, example2 }, 0x07)
                : isArray1 ? new CallArrayBag1(new[] { example1, example2 })
                : isList1 ? new CallListBag1(new List<Call> { example1, example2 })
                : isArray0 ? new[] { example1, example2 }
                : new List<Call> { example1, example2 };

            Func<ProtoConvert, object> creator = isArrayE ? r => r.ReadObject<Call[]>()
                : isListE ? r => r.ReadObject<List<Call>>()
                : isArray2 ? r => r.ReadObject<CallArrayBag2>()
                : isList2 ? r => r.ReadObject<CallListBag2>()
                : isArray1 ? r => r.ReadObject<CallArrayBag1>()
                : isList1 ? r => r.ReadObject<CallListBag1>()
                : isArray0 ? r => r.ReadObject<Call[]>()
                : r => r.ReadObject<List<Call>>();
            TestWrite(hex, value, creator);
        }

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
            var isErr = bigNumber == null && decimals == null;
            var isCall = bigNumber != null && decimals == null;
            var isRes = bigNumber == null && decimals != null;

            object value = isCall ? new Call("C" + bigNumber, name,
                    new object[] { number, "l" }, new[] { "i", "s" })
                : isErr ? new Invalid(number, name)
                : isRes ? new Result(name, (short)(decimals + number))
                : new Example(number, bigNumber!.Value, decimals!.Value, name);

            Func<ProtoConvert, object> creator = isCall ? r => r.ReadObject<Call>()
                : isErr ? r => r.ReadObject<Invalid>()
                : isRes ? r => r.ReadObject<Result>()
                : r => r.ReadObject<Example>();

            TestWrite(hex, value, creator);
        }

        internal static void TestWrite(string hex, object value, Func<ProtoConvert, object> creator)
        {
            var s = new ProtoSettings();

            using var writer = CreateWriter(out var mem, s);
            writer.WriteObject(value);
            var actual = ToHex(mem);
            Assert.Equal(hex, actual);

            using var reader = CreateReader(mem, s);
            var obj = creator(reader);

            if (obj is not Call && obj is not EnumTest.Texted && 
                obj is not Call[] && obj is not ICollection<Call> &&
                obj is not CallArrayBag1 && obj is not CallArrayBag2 &&
                obj is not CallListBag1 && obj is not CallListBag2)
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

        public record CallListBag2(IList<Call> Calls, byte Ord);
        public record CallListBag1(IList<Call> Calls);
        public record CallArrayBag2(Call[] Calls, byte Ord);
        public record CallArrayBag1(Call[] Calls);
    }
}