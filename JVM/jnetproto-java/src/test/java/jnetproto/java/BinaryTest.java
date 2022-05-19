package jnetproto.java;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public final class BinaryTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
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
                        // Short
                        {"000080", (short) -32768},
                        {"0000C0", (short) -16384},
                        {"000000", (short) 0},
                        {"00FF3F", (short) 16383},
                        {"00FF7F", (short) 32767},
                        // Int
                        {"0000000080", -2147483648},
                        {"00010000C0", -1073741823},
                        {"0000000000", 0},
                        {"00FFFFFF3F", 1073741823},
                        {"00FFFFFF7F", 2147483647},
                        // Long
                        {"000000000000000080", -9223372036854775808L},
                        {"0000000000000000C0", -4611686018427387904L},
                        {"000000000000000000", 0L},
                        {"00FFFFFFFFFFFFFF3F", 4611686018427387903L},
                        {"00FFFFFFFFFFFFFF7F", 9223372036854775807L},
                        // Float
                        {"00FFFF7FFF", -3.4028235E+38f},
                        {"00FFFFFFFE", -1.7014117E+38f},
                        {"0000000000", 0f},
                        {"00FFFFFF7E", 1.7014117E+38f},
                        {"00FFFF7F7F", 3.4028235E+38f},
                        // Double
                        {"00FFFFFFFFFFFFEFFF", -1.7976931348623157E+308d},
                        {"00FFFFFFFFFFFFDFFF", -8.988465674311579E+307},
                        {"000000000000000000", 0d},
                        {"00FFFFFFFFFFFFDF7F", 8.988465674311579E+307},
                        {"00FFFFFFFFFFFFEF7F", 1.7976931348623157E+308d},
                        // Decimal
                        {"001E2D3739323238313632353134323634333337353933353433393530333335", "-79228162514264337593543950335m"},
                        {"00022D31", "-1m"},
                        {"000130", "0m"},
                        {"000131", "1m"},
                        {"001D3739323238313632353134323634333337353933353433393530333335", "79228162514264337593543950335m"},
                        // TimeSpan
                        {"0028431CEBE2360AC3", "PT-256204778H-48M-5.477St"},
                        {"000000000000000000", "PT0St"},
                        {"0028431CEBE2360A43", "PT256204778H48M5.477St"},
                        // DateTime
                        {"0000096E88F1FFFFFF", "0001-01-01T00:00:00.0000000d"},
                        {"004F98346200000000", "2022-03-18T14:33:51.4722797d"},
                        {"006F33F4FF3A000000", "9999-12-31T22:59:59.9999999d"},
                        // Guid
                        {"0000000000000000000000000000000000", "00000000-0000-0000-0000-000000000000g"},
                        {"00F234CA7D8C13DA45BE396595E432F529", "7dca34f2-138c-45da-be39-6595e432f529g"}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void shouldWrite(String expected, Object value) throws Exception {
        var txt = value.toString();
        if (txt.endsWith("m"))
            value = new BigDecimal(txt.replace('m', ' ').trim());
        else if (txt.endsWith("t"))
            value = Duration.parse(txt.replace('t', ' ').trim());
        else if (txt.endsWith("d"))
            value = LocalDateTime.parse(txt.replace('d', ' ').trim());
        else if (txt.endsWith("g"))
            value = UUID.fromString(txt.replace('g', ' ').trim());
        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem)) {
            writer.writeObject(value);
            var actual = "00" + toHex(mem[0]).substring(2);
            assertEquals(actual, expected);
        }
    }

    private static String toHex(ByteArrayOutputStream mem) {
        var txt = Hex.encodeHexString(mem.toByteArray());
        return txt.toUpperCase();
    }

    private static IDataWriter createWriter(ByteArrayOutputStream[] mem) {
        return new BinaryWriter(mem[0] = new ByteArrayOutputStream());
    }
}
