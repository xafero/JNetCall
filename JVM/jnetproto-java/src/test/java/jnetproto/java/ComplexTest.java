package jnetproto.java;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ComplexTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        {"220000001304040A0000000504000000000000000700000000000014400A0774687269667479", 10, 4L, 5d, "thrifty"},
                        {"1E0000001304040500000005030000000000000007000000000000F03F0A036E6F74", 5, 3L, 1d, "not"},
                        {"100000001302040A0000000A0774687269667479", 10, null, null, "thrifty"},
                        {"0C000000130204050000000A036E6F74", 5, null, null, "not"}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void ShouldWrite(String hex, int number, Long bigNumber, Double decimals, String name)
            throws Exception {
        var s = new ProtoSettings();

        var isErr = bigNumber == null && decimals == null;
        Object value = isErr
                ? new Invalid(number, name)
                : new Example(number, bigNumber, decimals, name);

        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem, s)) {
            writer.writeObject(value);
            var actual = BinaryTest.toHex(mem[0]);
            assertEquals(actual, hex);

            try (var reader = createReader(mem[0], s)) {
                Object obj = isErr
                        ? reader.readObject(Invalid.class)
                        : reader.readObject(Example.class);
                assertEquals(value, obj);
            }
        }
    }

    private static ProtoConvert createWriter(ByteArrayOutputStream[] mem, ProtoSettings s)
    {
        return new ProtoConvert(null, mem[0] = new ByteArrayOutputStream(), s);
    }

    private static ProtoConvert createReader(ByteArrayOutputStream mem, ProtoSettings s)
    {
        return new ProtoConvert(new ByteArrayInputStream(mem.toByteArray()), null, s);
    }

    public record Example(int Number, long BigNumber, double Decimals, String Name) { }
    public record Invalid(int What, String Why) { }
}
