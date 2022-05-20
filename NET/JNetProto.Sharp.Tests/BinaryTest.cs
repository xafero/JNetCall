// ReSharper disable StringLiteralTypo
using System;
using System.Collections.Generic;
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
        [InlineData("0101", true)]
        [InlineData("0100", false)]
        [InlineData("0E0101020000000001", new[] { false, true })]
        // Byte
        [InlineData("0280", unchecked((byte)-128))]
        [InlineData("02C0", unchecked((byte)-64))]
        [InlineData("0200", (byte)0)]
        [InlineData("023F", (byte)63)]
        [InlineData("027F", (byte)127)]
        [InlineData("02FF", (byte)255)]
        [InlineData("0E020102000000F32A", new byte[] { unchecked((byte)-13), 42 })]
        [InlineData("0E0201020000000DD6", new byte[] { 13, unchecked((byte)-42) })]
        // Short
        [InlineData("030080", (short)-32768)]
        [InlineData("0300C0", (short)-16384)]
        [InlineData("030000", (short)0)]
        [InlineData("03FF3F", (short)16383)]
        [InlineData("03FF7F", (short)32767)]
        [InlineData("0E030102000000F3FF2A00", new short[] { -13, 42 })]
        // Int
        [InlineData("0400000080", -2147483648)]
        [InlineData("04010000C0", -1073741823)]
        [InlineData("0400000000", 0)]
        [InlineData("04FFFFFF3F", 1073741823)]
        [InlineData("04FFFFFF7F", 2147483647)]
        [InlineData("0E040102000000F3FFFFFF2A000000", new [] { -13, 42 })]
        // Long
        [InlineData("050000000000000080", -9223372036854775808L)]
        [InlineData("0500000000000000C0", -4611686018427387904L)]
        [InlineData("050000000000000000", 0L)]
        [InlineData("05FFFFFFFFFFFFFF3F", 4611686018427387903L)]
        [InlineData("05FFFFFFFFFFFFFF7F", 9223372036854775807L)]
        [InlineData("0E050102000000F3FFFFFFFFFFFFFF2A00000000000000", new long[] { -13, 42 })]
        // Float
        [InlineData("06FFFF7FFF", -3.4028235E+38f)]
        [InlineData("06FFFFFFFE", -1.7014117E+38f)]
        [InlineData("0600000000", 0f)]
        [InlineData("06FFFFFF7E", 1.7014117E+38f)]
        [InlineData("06FFFF7F7F", 3.4028235E+38f)]
        [InlineData("0E060102000000CDCC54C1CDCC2942", new[] { -13.3f, 42.45f })]
        // Double
        [InlineData("07FFFFFFFFFFFFEFFF", -1.7976931348623157E+308d)]
        [InlineData("07FFFFFFFFFFFFDFFF", -8.988465674311579E+307)]
        [InlineData("070000000000000000", 0d)]
        [InlineData("07FFFFFFFFFFFFDF7F", 8.988465674311579E+307)]
        [InlineData("07FFFFFFFFFFFFEF7F", 1.7976931348623157E+308d)]
        [InlineData("0E0701020000009A99999999992AC09A99999999394540", new [] { -13.3, 42.45 })]
        // Decimal
        [InlineData("081E2D3739323238313632353134323634333337353933353433393530333335", "-79228162514264337593543950335m")]
        [InlineData("08022D31", "-1m")]
        [InlineData("080130", "0m")]
        [InlineData("080131", "1m")]
        [InlineData("081D3739323238313632353134323634333337353933353433393530333335", "79228162514264337593543950335m")]
        [InlineData("0E0801020000000431332E330534322E3435", "1;13.3m;42.45m")]
        // TimeSpan
        [InlineData("0B28431CEBE2360AC3", "-10675199.02:48:05.4769664t")]
        [InlineData("0B0000000000000000", "00:00:00t")]
        [InlineData("0B28431CEBE2360A43", "10675199.02:48:05.4769664t")]
        [InlineData("0E0B0102000000000000007C674C4100000000AA0C6CC1", "1;01:02:03t;-04:05:06t")]
        // DateTime
        [InlineData("0C00096E88F1FFFFFF00000000", "0001-01-01T00:00:00.0000000d")]
        [InlineData("0C4F983462000000006D104800", "2022-03-18T14:33:51.4722797d")]
        [InlineData("0C6F33F4FF3A0000007F969800", "9999-12-31T22:59:59.9999999d")]
        [InlineData("0E0C010200000000D2585C000000000000000080C8965C0000000000000000", "1;2019-02-05d;2019-03-24d")]
        // Guid
        [InlineData("0D00000000000000000000000000000000", "00000000-0000-0000-0000-000000000000g")]
        [InlineData("0DF234CA7D8C13DA45BE396595E432F529", "7dca34f2-138c-45da-be39-6595e432f529g")]
        [InlineData("0E0D010200000092BA70EEFF8D7E4CA6C9081BBA58EA0274BC0781FF1C0549990703601697CEA1", "1;ee70ba92-8dff-4c7e-a6c9-081bba58ea02g;8107bc74-1cff-4905-9907-03601697cea1g")]
        // Char
        [InlineData("092000", ' ')]
        [InlineData("095F00", '_')]
        [InlineData("0E09010200000020005F00", new[] { ' ', '_' })]
        // String
        [InlineData("0A00", "")]
        [InlineData("0A015F", "_")]
        [InlineData("0E0A010200000000015F", new[] {  "", "_" })]
        // Map
        [InlineData("0F0A0A0200000001660448616E73016C084D61756C77757266", new object[] { 'M', "f", "Hans", "l", "Maulwurf" })]
        [InlineData("0F0A0602000000064265726C696EAE4769400748616D62757267E3A5EB3F", new object[] { 'M', "Berlin", 3.645f, "Hamburg", 1.841f })]
        [InlineData("0F020A0200000002064D6F6E646179050653756E646179", new object[] { 'M', (byte)2, "Monday", (byte)5, "Sunday" })]
        [InlineData("0F0A0202000000064D6F6E646179020653756E64617905", new object[] { 'M', "Monday", (byte)2, "Sunday", (byte)5 })]
        public void ShouldWrite(string expected, object value)
        {
            using var writer = CreateWriter(out var mem);
            writer.WriteObject(value = GetValue(value));
            var actual = ToHex(mem);
            Assert.Equal(expected, actual);

            using var reader = CreateReader(mem);
            var obj = reader.ReadObject();
            Assert.Equal(value, obj);
        }

        private static object GetValue(object value)
        {
            var txt = value.ToString()!;
            if (value is object[] objects)
            {
                if (objects[0] is 'M')
                {
                    var keyType = objects[1].GetType();
                    var valType = objects[2].GetType();
                    var dictType = typeof(Dictionary<,>).MakeGenericType(keyType, valType);
                    var dict = Activator.CreateInstance(dictType)!;
                    dynamic dictDyn = dict;
                    for (var i = 1; i < objects.Length; i += 2)
                    {
                        dynamic key = objects[i];
                        dynamic val = objects[i + 1];
                        dictDyn[key] = val;
                    }
                    value = dict;
                }
            }
            else if (txt.StartsWith("1;"))
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