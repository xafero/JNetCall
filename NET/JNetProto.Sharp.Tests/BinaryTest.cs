// ReSharper disable StringLiteralTypo
using System;
using System.Globalization;
using System.IO;
using System.Linq;
using Xunit;
using static System.Globalization.DateTimeStyles;

namespace JNetProto.Sharp.Tests
{
    public class BinaryTest
    {
        [Theory]
        // Bool
        [InlineData("0001", true)]
        [InlineData("0000", false)]
        [InlineData("000101020000000001", new[] { false, true })]
        // Byte
        [InlineData("0000", (byte)0)]
        [InlineData("007F", (byte)127)]
        [InlineData("00FF", (byte)255)]
        [InlineData("000201020000000D2A", new byte[] { 13, 42 })]
        // SByte
        [InlineData("0080", (sbyte)-128)]
        [InlineData("00C0", (sbyte)-64)]
        [InlineData("0000", (sbyte)0)]
        [InlineData("003F", (sbyte)63)]
        [InlineData("007F", (sbyte)127)]
        [InlineData("00030102000000F32A", new sbyte[] { -13, 42 })]
        // Short
        [InlineData("000080", (short)-32768)]
        [InlineData("0000C0", (short)-16384)]
        [InlineData("000000", (short)0)]
        [InlineData("00FF3F", (short)16383)]
        [InlineData("00FF7F", (short)32767)]
        [InlineData("00040102000000F3FF2A00", new short[] { -13, 42 })]
        // Int
        [InlineData("0000000080", -2147483648)]
        [InlineData("00010000C0", -1073741823)]
        [InlineData("0000000000", 0)]
        [InlineData("00FFFFFF3F", 1073741823)]
        [InlineData("00FFFFFF7F", 2147483647)]
        [InlineData("00050102000000F3FFFFFF2A000000", new [] { -13, 42 })]
        // Long
        [InlineData("000000000000000080", -9223372036854775808L)]
        [InlineData("0000000000000000C0", -4611686018427387904L)]
        [InlineData("000000000000000000", 0L)]
        [InlineData("00FFFFFFFFFFFFFF3F", 4611686018427387903L)]
        [InlineData("00FFFFFFFFFFFFFF7F", 9223372036854775807L)]
        [InlineData("00060102000000F3FFFFFFFFFFFFFF2A00000000000000", new long[] { -13, 42 })]
        // Float
        [InlineData("00FFFF7FFF", -3.4028235E+38f)]
        [InlineData("00FFFFFFFE", -1.7014117E+38f)]
        [InlineData("0000000000", 0f)]
        [InlineData("00FFFFFF7E", 1.7014117E+38f)]
        [InlineData("00FFFF7F7F", 3.4028235E+38f)]
        [InlineData("00070102000000CDCC54C1CDCC2942", new[] { -13.3f, 42.45f })]
        // Double
        [InlineData("00FFFFFFFFFFFFEFFF", -1.7976931348623157E+308d)]
        [InlineData("00FFFFFFFFFFFFDFFF", -8.988465674311579E+307)]
        [InlineData("000000000000000000", 0d)]
        [InlineData("00FFFFFFFFFFFFDF7F", 8.988465674311579E+307)]
        [InlineData("00FFFFFFFFFFFFEF7F", 1.7976931348623157E+308d)]
        [InlineData("000801020000009A99999999992AC09A99999999394540", new [] { -13.3, 42.45 })]
        // Decimal
        [InlineData("001E2D3739323238313632353134323634333337353933353433393530333335", "-79228162514264337593543950335m")]
        [InlineData("00022D31", "-1m")]
        [InlineData("000130", "0m")]
        [InlineData("000131", "1m")]
        [InlineData("001D3739323238313632353134323634333337353933353433393530333335", "79228162514264337593543950335m")]
        [InlineData("000901020000000431332E330534322E3435", "1;13.3m;42.45m")]
        // TimeSpan
        [InlineData("0028431CEBE2360AC3", "-10675199.02:48:05.4769664t")]
        [InlineData("000000000000000000", "00:00:00t")]
        [InlineData("0028431CEBE2360A43", "10675199.02:48:05.4769664t")]
        [InlineData("000C0102000000000000007C674C4100000000AA0C6CC1", "1;01:02:03t;-04:05:06t")]
        // DateTime
        [InlineData("0000096E88F1FFFFFF00000000", "0001-01-01T00:00:00.0000000d")]
        [InlineData("004F983462000000006D104800", "2022-03-18T14:33:51.4722797d")]
        [InlineData("006F33F4FF3A0000007F969800", "9999-12-31T22:59:59.9999999d")]
        [InlineData("000D010200000000D2585C000000000000000080C8965C0000000000000000", "1;2019-02-05d;2019-03-24d")]
        // Guid
        [InlineData("0000000000000000000000000000000000", "00000000-0000-0000-0000-000000000000g")]
        [InlineData("00F234CA7D8C13DA45BE396595E432F529", "7dca34f2-138c-45da-be39-6595e432f529g")]
        [InlineData("000E010200000092BA70EEFF8D7E4CA6C9081BBA58EA0274BC0781FF1C0549990703601697CEA1", "1;ee70ba92-8dff-4c7e-a6c9-081bba58ea02g;8107bc74-1cff-4905-9907-03601697cea1g")]
        // Char
        [InlineData("002000", ' ')]
        [InlineData("005F00", '_')]
        [InlineData("000A010200000020005F00", new[] { ' ', '_' })]
        // String
        [InlineData("0000", "")]
        [InlineData("00015F", "_")]
        [InlineData("000B010200000000015F", new[] {  "", "_" })]
        public void ShouldWrite(string expected, object value)
        {
            using var writer = CreateWriter(out var mem);
            writer.WriteObject(value = GetValue(value));
            var actual = "00" + ToHex(mem).Substring(2);
            Assert.Equal(expected, actual);

            using var reader = CreateReader(mem);
            var obj = reader.ReadObject();
            Assert.Equal(value, obj);
        }

        private static object GetValue(object value)
        {
            var txt = value.ToString()!;
            if (txt.StartsWith("1;"))
            {
                var oneArray = txt.Substring(2).Split(";").Select(GetValue).ToArray();
                var array = Array.CreateInstance(oneArray[0].GetType(), oneArray.Length);
                for (var i = 0; i < array.Length; i++)
                    array.SetValue(oneArray[i], i);
                value = array;
            }
            else if (txt.EndsWith("m"))
            {
                var inv = CultureInfo.InvariantCulture;
                value = decimal.Parse(txt.Replace('m', ' ').Trim(), inv);
            }
            else if (txt.EndsWith("t"))
                value = TimeSpan.Parse(txt.Replace('t', ' ').Trim());
            else if (txt.EndsWith("d"))
                value = DateTime.Parse(txt.Replace('d', ' ').Trim(),
                    styles: AssumeUniversal).ToUniversalTime();
            else if (txt.EndsWith("g"))
                value = Guid.Parse(txt.Replace('g', ' ').Trim());
            return value;
        }

        private static string ToHex(MemoryStream mem)
        {
            var txt = BitConverter.ToString(mem.ToArray());
            return txt.Replace("-", string.Empty);
        }

        private static IDataWriter CreateWriter(out MemoryStream mem)
        {
            return new BinaryWriter(mem = new MemoryStream());
        }

        private static IDataReader CreateReader(MemoryStream mem)
        {
            return new BinaryReader(new MemoryStream(mem.ToArray()));
        }
    }
}