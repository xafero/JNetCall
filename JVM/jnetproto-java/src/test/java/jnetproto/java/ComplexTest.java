package jnetproto.java;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.google.gson.GsonBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ComplexTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        {"2100000013040A034333330A035365741302042A0000000A016C0E0A010200000001690173", 42, 33L, null, "Set"},
                        {"2000000013040A0243370A03476574130204030000000A016C0E0A010200000001690173", 3, 7L, null, "Get"},
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
        var isCall = bigNumber != null && decimals == null;
        Object value = isCall ? new Call("C" + bigNumber, name,
                new Object[] { number, "l" }, new String[] { "i", "s" })
            : isErr ? new Invalid(number, name)
            : new Example(number, bigNumber, decimals, name);
        
        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem, s)) {
            writer.writeObject(value);
            var actual = BinaryTest.toHex(mem[0]);
            assertEquals(actual, hex);

            try (var reader = createReader(mem[0], s)) {
                Object obj = isCall ? reader.readObject(Call.class)
                        : isErr ? reader.readObject(Invalid.class)
                        : reader.readObject(Example.class);
                
                if (!(obj instanceof Call))
                {
                    assertEquals(value, obj);
                    return;
                }
                var gson = (new GsonBuilder()).create();
                var valueJson = gson.toJson(value);
                var objJson = gson.toJson(obj);
                assertEquals(valueJson, objJson);
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
    public record Call(String C, String M, Object[] A, String[] H) { }
}
