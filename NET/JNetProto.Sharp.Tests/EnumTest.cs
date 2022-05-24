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
        public void ShouldWrite(string hex, Season what, string why, ErrorCode? code, Days? days)
        {
            var bitty = new Bitty(what, why, code ?? default, days ?? default);
            TestWrite(hex, bitty, r => r.ReadObject<Bitty>());
        }

        public record Bitty(Season What, string Why, ErrorCode Code, Days Days);

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