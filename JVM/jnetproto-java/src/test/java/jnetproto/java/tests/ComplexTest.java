package jnetproto.java.tests;

import com.google.gson.GsonBuilder;
import jnetbase.java.meta.TypeToken;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.testng.Assert.assertEquals;

public final class ComplexTest {

    @DataProvider(name = "writeArgs2")
    public Object[][] getWriteArgs2() {
        return new Object[][]
        {
            {"380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'C'},
            {"380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'N'},
            {"360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'B'},
            {"360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'M'},
            {"34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'A'},
            {"34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'L'}
        };
    }

    @Test(dataProvider = "writeArgs2")
    public void ShouldColl(String hex, char mode) throws Exception {
        var isArray0 = mode == 'A';
        var isList1 = mode == 'M';
        var isArray1 = mode == 'B';
        var isList2 = mode == 'N';
        var isArray2 = mode == 'C';

        var example1 = new Call("c", "m", new Object[] { 'a' }, new String[] { "h" });
        var example2 = new Call("d", "n", new Object[] { 'b' }, new String[] { "i" });

        Object value = isArray2 ? new CallArrayBag2(new Call[] { example1, example2 }, (byte)0x07)
                : isList2 ? new CallListBag2(Arrays.asList(  example1, example2  ), (byte)0x07)
                : isArray1 ? new CallArrayBag1(new Call[] { example1, example2 })
                : isList1 ? new CallListBag1( Arrays.asList( example1, example2  ))
                : isArray0 ? new Call[] { example1, example2 }
                : Arrays.asList(  example1, example2  );

        Function<ProtoConvert, Object> creator = r -> {
            try {
                return isArray2 ?  r.readObject(CallArrayBag2.class)
                : isList2 ?   r.readObject(CallListBag2.class)
                : isArray1 ?   r.readObject(CallArrayBag1.class)
                : isList1 ?   r.readObject(CallListBag1.class)
                : isArray0 ?  r.readObject(Call[].class)
                :     r.readObject(  new TypeToken<List<Call>>() {}  );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        testWrite(hex, value, creator);
    }

    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        {"1400000013020A0C007468697320697320676F6F64030800", 3, null, 5d, "this is good"},
                        {"0C00000013020A040063726170030600", 2, null, 4d, "crap"},
                        {"2600000013040A03004333330A03005365741302042A0000000A01006C0E0A0102000000010069010073", 42, 33L, null, "Set"},
                        {"2500000013040A020043370A0300476574130204030000000A01006C0E0A0102000000010069010073", 3, 7L, null, "Get"},
                        {"230000001304040A0000000504000000000000000700000000000014400A070074687269667479", 10, 4L, 5d, "thrifty"},
                        {"1F0000001304040500000005030000000000000007000000000000F03F0A03006E6F74", 5, 3L, 1d, "not"},
                        {"110000001302040A0000000A070074687269667479", 10, null, null, "thrifty"},
                        {"0D000000130204050000000A03006E6F74", 5, null, null, "not"}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void ShouldWrite(String hex, int number, Long bigNumber, Double decimals, String name)
            throws Exception {
        var isErr = bigNumber == null && decimals == null;
        var isCall = bigNumber != null && decimals == null;
        var isRes = bigNumber == null && decimals != null;

        Object value = isCall ? new Call("C" + bigNumber, name,
                new Object[]{number, "l"}, new String[]{"i", "s"})
                : isErr ? new Invalid(number, name)
                : isRes ? new Result(name, (short) (decimals + number))
                : new Example(number, bigNumber, decimals, name);

        Function<ProtoConvert, Object> creator = r -> {
            try {
                return isCall ? r.readObject(Call.class)
                        : isErr ? r.readObject(Invalid.class)
                        : isRes ? r.readObject(Result.class)
                        : r.readObject(Example.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        testWrite(hex, value, creator);
    }

    static void testWrite(String hex, Object value, Function<ProtoConvert, Object> creator)
            throws Exception {
        var s = new ProtoSettings();

        var mem = new ByteArrayOutputStream[1];
        try (var writer = createWriter(mem, s)) {
            writer.writeObject(value);
            var actual = BinaryTest.toHex(mem[0]);
            assertEquals(actual, hex);

            try (var reader = createReader(mem[0], s)) {
                Object obj = creator.apply(reader);

                if (!(obj instanceof Call) && !(obj instanceof EnumTest.Texted) &&
                        !(obj instanceof Call[]) && !(obj instanceof Collection) &&
                        !(obj instanceof CallArrayBag1) && !(obj instanceof CallArrayBag2) &&
                        !(obj instanceof CallListBag1) && !(obj instanceof CallListBag2))
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
    public record Result(Object R, short S) { }

    public record CallListBag2(List<Call> Calls, byte Ord) { }
    public record CallListBag1(List<Call> Calls) { }
    public record CallArrayBag2(Call[] Calls, byte Ord) { }
    public record CallArrayBag1(Call[] Calls) { }
}
