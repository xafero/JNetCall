package jnetproto.java.tests;

import jnetproto.java.api.*;
import jnetbase.java.*;
import jnetproto.java.core.BinaryReader;
import jnetproto.java.core.BinaryWriter;
import jnetproto.java.tools.*;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.testng.Assert.assertEquals;

public final class BinaryTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        // Bool
                        {"0101", true},
                        {"0100", false},
                        {"0E0101020000000001", new boolean[]{false, true}},
                        // Byte
                        {"0280", (byte) -128},
                        {"02C0", (byte) -64},
                        {"0200", (byte) 0},
                        {"023F", (byte) 63},
                        {"027F", (byte) 127},
                        {"02FF", (byte) 255},
                        // Binary
                        {"1402000000F32A", new byte[]{-13, 42}},
                        {"14020000000DD6", new byte[]{13, -42}},
                        {"140A00000001020304050607080900", new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}},
                        // Short
                        {"030080", (short) -32768},
                        {"0300C0", (short) -16384},
                        {"030000", (short) 0},
                        {"03FF3F", (short) 16383},
                        {"03FF7F", (short) 32767},
                        {"0E030102000000F3FF2A00", new short[]{-13, 42}},
                        // Int
                        {"0400000080", -2147483648},
                        {"04010000C0", -1073741823},
                        {"0400000000", 0},
                        {"04FFFFFF3F", 1073741823},
                        {"04FFFFFF7F", 2147483647},
                        {"0E040102000000F3FFFFFF2A000000", new int[]{-13, 42}},
                        // Long
                        {"050000000000000080", -9223372036854775808L},
                        {"0500000000000000C0", -4611686018427387904L},
                        {"050000000000000000", 0L},
                        {"05FFFFFFFFFFFFFF3F", 4611686018427387903L},
                        {"05FFFFFFFFFFFFFF7F", 9223372036854775807L},
                        {"0E050102000000F3FFFFFFFFFFFFFF2A00000000000000", new long[]{-13, 42}},
                        // Float
                        {"06FFFF7FFF", -3.4028235E+38f},
                        {"06FFFFFFFE", -1.7014117E+38f},
                        {"0600000000", 0f},
                        {"06FFFFFF7E", 1.7014117E+38f},
                        {"06FFFF7F7F", 3.4028235E+38f},
                        {"0E060102000000CDCC54C1CDCC2942", new float[]{-13.3f, 42.45f}},
                        // Double
                        {"07FFFFFFFFFFFFEFFF", -1.7976931348623157E+308d},
                        {"07FFFFFFFFFFFFDFFF", -8.988465674311579E+307},
                        {"070000000000000000", 0d},
                        {"07FFFFFFFFFFFFDF7F", 8.988465674311579E+307},
                        {"07FFFFFFFFFFFFEF7F", 1.7976931348623157E+308d},
                        {"0E0701020000009A99999999992AC09A99999999394540", new double[]{-13.3, 42.45}},
                        // Decimal
                        {"081E2D3739323238313632353134323634333337353933353433393530333335", "-79228162514264337593543950335m"},
                        {"08022D31", "-1m"},
                        {"080130", "0m"},
                        {"080131", "1m"},
                        {"081D3739323238313632353134323634333337353933353433393530333335", "79228162514264337593543950335m"},
                        {"0E0801020000000431332E330534322E3435", "1;13.3m;42.45m"},
                        // TimeSpan
                        {"0B28431CEBE2360AC3", "PT-256204778H-48M-5.477St"},
                        {"0B0000000000000000", "PT0St"},
                        {"0B28431CEBE2360A43", "PT256204778H48M5.477St"},
                        {"0E0B0102000000000000007C674C4100000000AA0C6CC1", "1;PT1H2M3St;PT-4H-5M-6St"},
                        // DateTime
                        {"0C00096E88F1FFFFFF00000000", "0001-01-01T00:00:00.0000000d"},
                        {"0C4F983462000000006D104800", "2022-03-18T14:33:51.4722797d"},
                        {"0C6F33F4FF3A0000007F969800", "9999-12-31T22:59:59.9999999d"},
                        {"0E0C010200000000D2585C000000000000000080C8965C0000000000000000", "1;2019-02-05d;2019-03-24d"},
                        // Guid
                        {"0D00000000000000000000000000000000", "00000000-0000-0000-0000-000000000000g"},
                        {"0DF234CA7D8C13DA45BE396595E432F529", "7dca34f2-138c-45da-be39-6595e432f529g"},
                        {"0E0D010200000092BA70EEFF8D7E4CA6C9081BBA58EA0274BC0781FF1C0549990703601697CEA1", "1;ee70ba92-8dff-4c7e-a6c9-081bba58ea02g;8107bc74-1cff-4905-9907-03601697cea1g"},
                        // Char
                        {"092000", ' '},
                        {"095F00", '_'},
                        {"0E09010200000020005F00", new char[]{' ', '_'}},
                        // String
                        {"0A0000", ""},
                        {"0A01005F", "_"},
                        {"0E0A0102000000000001005F", new String[]{"", "_"}},
                        // Wide String
                        {"0AFE007878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878", "254w"},
                        {"0AFF00787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878", "255w"},
                        {"0A000178787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878", "256w"},
                        {"0E0A0103000000FE007878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878FF00787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878000178787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878787878", "1;254w;255w;256w"},
                        // Map
                        {"0F0A0A02000000010066040048616E7301006C08004D61756C77757266", new Object[]{'M', "f", "Hans", "l", "Maulwurf"}},
                        {"0F0A060200000006004265726C696EAE476940070048616D62757267E3A5EB3F", new Object[]{'M', "Berlin", 3.645f, "Hamburg", 1.841f}},
                        {"0F020A020000000206004D6F6E64617905060053756E646179", new Object[]{'M', (byte) 2, "Monday", (byte) 5, "Sunday"}},
                        {"0F0A020200000006004D6F6E64617902060053756E64617905", new Object[]{'M', "Monday", (byte) 2, "Sunday", (byte) 5}},
                        // Tuple
                        {"10010101", new Object[]{'T', true}},
                        {"100201010402000000", new Object[]{'T', true, 2}},
                        {"100301010402000000050300000000000000", new Object[]{'T', true, 2, 3L}},
                        {"1004010104020000000503000000000000000600008040", new Object[]{'T', true, 2, 3L, 4f}},
                        {"1005010104020000000503000000000000000600008040070000000000001440", new Object[]{'T', true, 2, 3L, 4f, 5d}},
                        {"1006010104020000000503000000000000000600008040070000000000001440093600", new Object[]{'T', true, 2, 3L, 4f, 5d, '6'}},
                        {"10070101040200000005030000000000000006000080400700000000000014400936000A010037", new Object[]{'T', true, 2, 3L, 4f, 5d, '6', "7"}},
                        {"10080101040200000005030000000000000006000080400700000000000014400936000A0100370100", new Object[]{'T', true, 2, 3L, 4f, 5d, '6', "7", false}},
                        // Set
                        {"1103010000000200", new Object[]{'S', (short) 2}},
                        {"11030200000002000300", new Object[]{'S', (short) 2, (short) 3}},
                        {"11030200000002000300", new Object[]{'S', (short) 2, (short) 3, (short) 3}},
                        // List
                        {"12060100000000000040", new Object[]{'L', 2f}},
                        {"1206020000000000004000004040", new Object[]{'L', 2f, 3f}},
                        {"120603000000000000400000404000004040", new Object[]{'L', 2f, 3f, 3f}},
                        // Bag
                        {"13010101", new Object[]{'B', true}},
                        {"130201010202", new Object[]{'B', true, (byte) 2}},
                        {"130301010202030300", new Object[]{'B', true, (byte) 2, (short) 3}},
                        // Null
                        {"15", null}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void shouldWrite(String expected, Object value) throws Exception {
        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem)) {
            writer.writeObject(value = getValue(value));
            var actual = toHex(mem[0]);
            assertEquals(actual, expected);

            try (var reader = createReader(mem[0])) {
                var obj = reader.readObject();
                assertEquals(obj, value);
            }
        }
    }

    private static Object getValue(Object value) {
        var txt = value == null ? "" : value.toString();
        if (value instanceof Object[] objects) {
            if (objects[0] instanceof Character c && c == 'M') {
                var dict = new HashMap<Object, Object>();
                for (var i = 1; i < objects.length; i += 2) {
                    var key = objects[i];
                    var val = objects[i + 1];
                    dict.put(key, val);
                }
                return dict;
            }
            else if (objects[0] instanceof Character c && c == 'T')
            {
                var tupArgs = Arrays.copyOfRange(objects, 1, objects.length);
                var creates = Arrays.stream(Tuples.class.getMethods());
                var create = creates.filter(m -> m.getParameterCount() == tupArgs.length);
                return Reflect.invoke(create.findFirst().get(), null, tupArgs);
            }
            else if (objects[0] instanceof Character c && c == 'B')
            {
                var args = Arrays.copyOfRange(objects, 1, objects.length);
                return args;
            }
            else if (objects[0] instanceof Character c && c == 'L')
            {
                var list = new LinkedList<Object>();
                for (var i = 1; i < objects.length; i++)
                {
                    var val = objects[i];
                    list.add(val);
                }
                return list;
            }
            else if (objects[0] instanceof Character c && c == 'S')
            {
                var set = new TreeSet<Object>();
                for (var i = 1; i < objects.length; i++)
                {
                    var val = objects[i];
                    set.add(val);
                }
                return set;
            }
        }
        if (txt.startsWith("1;")) {
            var parts = txt.substring(2).split(";");
            var oneArray = Arrays.stream(parts).map(s -> getValue(s)).toArray();
            var array = Array.newInstance(oneArray[0].getClass(), oneArray.length);
            for (var i = 0; i < oneArray.length; i++)
                Array.set(array, i, oneArray[i]);
            return array;
        }
        if (txt.endsWith("w"))
        {
            var count = Integer.parseInt(txt.replace('w', ' ').trim());
            return Strings.repeat(count, "x");
        }
        if (txt.endsWith("m")) {
            return new BigDecimal(txt.replace('m', ' ').trim());
        }
        if (txt.endsWith("t")) {
            return Duration.parse(txt.replace('t', ' ').trim());
        }
        if (txt.endsWith("d") && !txt.startsWith("[")) {
            var rawDate = txt.replace('d', ' ').trim();
            if (!rawDate.contains("T")) rawDate += "T00:00:00.0000000";
            return LocalDateTime.parse(rawDate);
        }
        if (txt.endsWith("g")) {
            return UUID.fromString(txt.replace('g', ' ').trim());
        }
        return value;
    }

    static String toHex(ByteArrayOutputStream mem) {
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
