package jnetproto.java;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public final class BinaryTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        // Bool
                        {"0001", true},
                        {"0000", false},
                        {"000101020000000001", new boolean[] { false, true }},
                        // Byte
                        {"0000", (byte) 0},
                        {"007F", (byte) 127},
                        {"00FF", (byte) 255},
                        // SByte
                        {"0080", (byte) -128},
                        {"00C0", (byte) -64},
                        {"0000", (byte) 0},
                        {"003F", (byte) 63},
                        {"007F", (byte) 127},
                        {"00030102000000F32A", new byte[] { -13, 42 }},
                        // Short
                        {"000080", (short) -32768},
                        {"0000C0", (short) -16384},
                        {"000000", (short) 0},
                        {"00FF3F", (short) 16383},
                        {"00FF7F", (short) 32767},
                        {"00040102000000F3FF2A00", new short[] { -13, 42 }},
                        // Int
                        {"0000000080", -2147483648},
                        {"00010000C0", -1073741823},
                        {"0000000000", 0},
                        {"00FFFFFF3F", 1073741823},
                        {"00FFFFFF7F", 2147483647},
                        {"00050102000000F3FFFFFF2A000000", new int[] { -13, 42 }},
                        // Long
                        {"000000000000000080", -9223372036854775808L},
                        {"0000000000000000C0", -4611686018427387904L},
                        {"000000000000000000", 0L},
                        {"00FFFFFFFFFFFFFF3F", 4611686018427387903L},
                        {"00FFFFFFFFFFFFFF7F", 9223372036854775807L},
                        {"00060102000000F3FFFFFFFFFFFFFF2A00000000000000", new long[] { -13, 42 }},
                        // Float
                        {"00FFFF7FFF", -3.4028235E+38f},
                        {"00FFFFFFFE", -1.7014117E+38f},
                        {"0000000000", 0f},
                        {"00FFFFFF7E", 1.7014117E+38f},
                        {"00FFFF7F7F", 3.4028235E+38f},
                        {"00070102000000CDCC54C1CDCC2942", new float[] { -13.3f, 42.45f }},
                        // Double
                        {"00FFFFFFFFFFFFEFFF", -1.7976931348623157E+308d},
                        {"00FFFFFFFFFFFFDFFF", -8.988465674311579E+307},
                        {"000000000000000000", 0d},
                        {"00FFFFFFFFFFFFDF7F", 8.988465674311579E+307},
                        {"00FFFFFFFFFFFFEF7F", 1.7976931348623157E+308d},
                        {"000801020000009A99999999992AC09A99999999394540", new double[] { -13.3, 42.45 }},
                        // Decimal
                        {"001E2D3739323238313632353134323634333337353933353433393530333335", "-79228162514264337593543950335m"},
                        {"00022D31", "-1m"},
                        {"000130", "0m"},
                        {"000131", "1m"},
                        {"001D3739323238313632353134323634333337353933353433393530333335", "79228162514264337593543950335m"},
                        {"000901020000000431332E330534322E3435", "1;13.3m;42.45m"},
                        // TimeSpan
                        {"0028431CEBE2360AC3", "PT-256204778H-48M-5.477St"},
                        {"000000000000000000", "PT0St"},
                        {"0028431CEBE2360A43", "PT256204778H48M5.477St"},
                        {"000C0102000000000000007C674C4100000000AA0C6CC1", "1;PT1H2M3St;PT-4H-5M-6St"},
                        // DateTime
                        {"0000096E88F1FFFFFF00000000", "0001-01-01T00:00:00.0000000d"},
                        {"004F983462000000006D104800", "2022-03-18T14:33:51.4722797d"},
                        {"006F33F4FF3A0000007F969800", "9999-12-31T22:59:59.9999999d"},
                        {"000D010200000000D2585C000000000000000080C8965C0000000000000000", "1;2019-02-05d;2019-03-24d"},
                        // Guid
                        {"0000000000000000000000000000000000", "00000000-0000-0000-0000-000000000000g"},
                        {"00F234CA7D8C13DA45BE396595E432F529", "7dca34f2-138c-45da-be39-6595e432f529g"},
                        {"000E010200000092BA70EEFF8D7E4CA6C9081BBA58EA0274BC0781FF1C0549990703601697CEA1", "1;ee70ba92-8dff-4c7e-a6c9-081bba58ea02g;8107bc74-1cff-4905-9907-03601697cea1g"},
                        // Char
                        {"002000", ' '},
                        {"005F00", '_'},
                        {"000A010200000020005F00", new char[] { ' ', '_' }},
                        // String
                        {"0000", ""},
                        {"00015F", "_"},
                        {"000B010200000000015F", new String[] {  "", "_" }},
                };
    }

    @Test(dataProvider = "writeArgs")
    public void shouldWrite(String expected, Object value) throws Exception {
        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem)) {
            writer.writeObject(value = getValue(value));
            var actual = "00" + toHex(mem[0]).substring(2);
            assertEquals(actual, expected);

            try (var reader = createReader(mem[0])) {
                var obj = reader.readObject();
                assertEquals(obj, value);
            }
        }
    }

    private static Object getValue(Object value) {
        var txt = value.toString();
        if (txt.startsWith("1;")) {
            var parts = txt.substring(2).split(";");
            var oneArray = Arrays.stream(parts).map(s -> getValue(s)).toArray();
            var array = Array.newInstance(oneArray[0].getClass(), oneArray.length);
            for (var i = 0; i < oneArray.length; i++)
                Array.set(array, i, oneArray[i]);
            value = array;
        } else if (txt.endsWith("m"))
            value = new BigDecimal(txt.replace('m', ' ').trim());
        else if (txt.endsWith("t"))
            value = Duration.parse(txt.replace('t', ' ').trim());
        else if (txt.endsWith("d")) {
            var rawDate = txt.replace('d', ' ').trim();
            if (!rawDate.contains("T")) rawDate += "T00:00:00.0000000";
            value = LocalDateTime.parse(rawDate);
        } else if (txt.endsWith("g"))
            value = UUID.fromString(txt.replace('g', ' ').trim());
        return value;
    }

    private static String toHex(ByteArrayOutputStream mem) {
        var txt = Hex.encodeHexString(mem.toByteArray());
        return txt.toUpperCase();
    }

    private static IDataWriter createWriter(ByteArrayOutputStream[] mem) {
        return new BinaryWriter(mem[0] = new ByteArrayOutputStream());
    }

    private static IDataReader createReader(ByteArrayOutputStream mem)
    {
        return new BinaryReader(new ByteArrayInputStream(mem.toByteArray()));
    }
}
