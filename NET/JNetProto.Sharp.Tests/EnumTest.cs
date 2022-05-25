using System;
using Xunit;
using static JNetProto.Sharp.Tests.ComplexTest;

namespace JNetProto.Sharp.Tests
{
    public class EnumTest
    {
        [Theory]
        [InlineData("13000000130404000000000A0100730300000415000000", Season.Spring, "s", ErrorCode.None, Days.Monday | Days.Wednesday | Days.Friday)]
        [InlineData("13000000130404020000000A01006103C8000418000000", Season.Autumn, "a", ErrorCode.OutlierReading, Days.Thursday | Days.Friday)]
        [InlineData("13000000130404030000000A0100770364000402000000", Season.Winter, "w", ErrorCode.ConnectionLost, Days.Tuesday)]
        [InlineData("13000000130404030000000A0100770300000400000000", Season.Winter, "w", null, null)]
        public void ShouldWrite1(string hex, Season what, string why, ErrorCode? code, Days? days)
        {
            var bitty = new Bitty(what, why, code ?? default, days ?? default);
            TestWrite(hex, bitty, r => r.ReadObject<Bitty>());
        }

        [Theory]
        [InlineData("2900000013030E04010200000001000000020000000E0A01020000000100680100770E030102000000C8006400", new[]{Season.Summer, Season.Autumn}, new[]{"h", "w"}, new[]{ErrorCode.OutlierReading, ErrorCode.ConnectionLost})]
        [InlineData("2000000013030E040101000000010000000E0A01010000000100680E030101000000C800", new[]{Season.Summer}, new[]{"h"}, new[]{ErrorCode.OutlierReading})]
        [InlineData("1700000013030E0401000000000E0A01000000000E030100000000", new Season[0], new string[0], new ErrorCode[0])]
        public void ShouldWrite2(string hex, Season[] whats, string[] whys, ErrorCode[] codes)
        {
            var texted = new Texted(whats, whys, codes); 
            TestWrite(hex, texted, r => r.ReadObject<Texted>());
        }

        public record Bitty(Season What, string Why, ErrorCode Code, Days Days);

        public record Texted(Season[] Whats, string[] Whys, ErrorCode[] Codes);

        public enum Season
        {
            Spring,
            Summer,
            Autumn,
            Winter
        }

        public enum ErrorCode : short
        {
            None = 0,
            Unknown = 1,
            ConnectionLost = 100,
            OutlierReading = 200
        }

        [Flags]
        public enum Days
        {
            None = 0b_0000_0000, // 0
            Monday = 0b_0000_0001, // 1
            Tuesday = 0b_0000_0010, // 2
            Wednesday = 0b_0000_0100, // 4
            Thursday = 0b_0000_1000, // 8
            Friday = 0b_0001_0000, // 16
            Saturday = 0b_0010_0000, // 32
            Sunday = 0b_0100_0000, // 64
            Weekend = Saturday | Sunday
        }
    }
}