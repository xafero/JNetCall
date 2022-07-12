package jnetcall.java.tests;

import jnetbase.java.*;
import org.example.api.*;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public abstract class CallTest {

    protected abstract <T extends AutoCloseable> T create(Class<T> clazz);

    @Test
    public void shouldCallCache() throws Exception {
        var input = new String[]{"life", "on", "mars"};
        try (var client = create(IStringCache.class)) {

            client.set(42, input[0]);
            assertEquals(1, client.getSize());
            assertEquals(input[0], client.get(42));

            client.delete(42);
            assertEquals(0, client.getSize());

            client.set(43, input[1]);
            client.set(44, input[2]);
            assertEquals(2, client.getSize());

            assertEquals(input[1], client.get(43));
            assertEquals(input[2], client.get(44));

            client.delete(43);
            client.delete(44);
            assertEquals(0, client.getSize());
        }
    }

    @Test
    public void shouldCallMultiple() throws Exception {
        try (var client = create(IMultiple.class)) {

            var t2T = client.GetTuple2T(200, "Greece");
            var t2V = client.GetTuple2V(t2T);
            assertEquals(t2T, t2V);

            var t3T = client.GetTuple3T(1, "cat", true);
            var t3V = client.GetTuple3V(t3T);
            assertEquals(t3T, t3V);

            var t4T = client.GetTuple4T("perl", new String[]{"java", "c#"}, 1, new int[]{2, 3});
            var t4V = client.GetTuple4V(t4T);
            assertEquals(t4T.getValue0(), t4V.getValue0());
            assertEquals(t4T.getValue1(), t4V.getValue1());
            assertEquals(t4T.getValue2(), t4V.getValue2());
            assertEquals(t4T.getValue3(), t4V.getValue3());

            var t5T = client.GetTuple5T(1, 1.5f, 2L, "dot", "net");
            var t5V = client.GetTuple5V(t5T);
            assertEquals(t5T, t5V);

            var bd1 = client.FindBestDay(3);
            assertEquals("Wednesday", bd1.toString());
            var bd2 = client.FindBestDay(5);
            assertEquals("Friday", bd2.toString());
            var bds = client.FindFreeDays();
            assertEquals("[Sunday, Thursday, Saturday]", bds.toString());

            var bdt = client.GetTextOf(new IMultiple.WeekDay[]{bd1, bd2}, bds);
            assertEquals("[Wednesday, Friday] | Sunday, Thursday, Saturday", bdt);
        }
    }

    @Test
    public void shouldCallDataTyped() throws Exception {
        try (var client = create(IDataTyped.class)) {

            var now = LocalDateTime.now();
            var dur = Duration.ofSeconds(94);
            var env = new HashMap<String, Integer>();
            env.put("Caller", "Program".length());
            var dict = client.GetSystemVariables(now, dur, env)
                    .entrySet().stream().map(f -> '['+f.getKey()+", "+f.getValue()+']').toArray(String[]::new);
            assertEquals("[Caller, 7]|[seconds, 94]|[year, 2022]", Strings.join("|", dict));

            var lines = Arrays.asList(new String[]{"Dog  ", "Hot", "Dog ", "Dog", "Hot    ", "Cat", "Cat", "Hot", "Hot"});
            var lineCount = client.GetLineCount(lines.toArray(String[]::new));
            var set = client.GetUnique(lines, true);
            assertEquals("Cat|Dog|Hot", Strings.join("|", set.toArray(String[]::new)));
            assertEquals(9, lineCount);

            var list = client.GetDouble(set).toArray(String[]::new);
            assertEquals("Cat|Dog|Hot|Cat|Dog|Hot", Strings.join("|", list));

            var fs = Arrays.stream(Primitives.castInt(client.AllocateBytes(3, (byte) 42)))
                    .boxed().map(f -> f.toString()).toArray(String[]::new);
            assertEquals("42|42|42", Strings.join("|", fs));

            var bsf = client.GetFileSize("Z:\\Nothing\\Good\\No fun with that file.txt");
            assertEquals(41, bsf);

            var ay = Byte.MAX_VALUE; var as = Short.MAX_VALUE; var ai = Integer.MAX_VALUE;
            var al = Long.MAX_VALUE; var af = Float.MAX_VALUE; var ad = Double.MAX_VALUE;
            var ab = true; var ac = Character.MAX_VALUE; var at = "Str";
            var au = BigDecimal.valueOf(Long.MAX_VALUE);
            var ag = UUID.fromString("27edb110-afef-4ce3-b8c1-3fcb2ec3fabe");
            var txt = client.ToSimpleText(ay, as, ai, al, af, ad, ab, ac, at, au, ag).replace(" ", "")
                    .trim().replace("3,4", "3.4").replace("1,7", "1.7");
            assertEquals("y=127,s=32767,i=2147483647,l=9223372036854775807,f=3.4028235E+38,d=1.7976931348623157E+308,b=True,c=￿,t=Str,u=9223372036854775807,g=27edb110-afef-4ce3-b8c1-3fcb2ec3fabe", txt);

            var by = new byte[]{ 42 }; var bs = new short[]{ Short.MIN_VALUE }; var bi = new int[]{ Integer.MIN_VALUE };
            var bl = new long[]{ Long.MIN_VALUE }; var bf = new float[]{ -3.4028235E38f };
            var bd = new double[]{ -1.7976931348623157E308d }; var bb = new boolean[]{ false };
            var bc = new char[]{ 'X' }; var bt = new String[]{ "Str1" };
            var bu = new BigDecimal[] { BigDecimal.valueOf(Long.MIN_VALUE) };
            var bg = new UUID[]{ UUID.fromString("00000000-0000-0000-0000-000000000000") };
            txt = client.ToArrayText(by, bs, bi, bl, bf, bd, bb, bc, bt, bu, bg).replace(" ", "")
                    .trim().replace("3,4", "3.4").replace("1,7", "1.7");
            assertEquals("y=[42],s=[-32768],i=[-2147483648],l=[-9223372036854775808],f=[-3.4028235E+38],d=[-1.7976931348623157E+308],b=[False],c=[X],t=[Str1],u=[-9223372036854775808],g=[00000000-0000-0000-0000-000000000000]", txt);
        }
    }

    @Test
    public void shouldCallSimultan() throws Exception {
        try (var client = create(ISimultaneous.class)) {

            client.loadIt("Hello").get();

            var id = client.getId().get();
            assertTrue(id >= -100 && id <= 100, id + " ?!");

            var txt = client.removeIt().get();
            assertEquals("Hello", txt);
        }
    }

    @Test
    public void shouldCallCalculator() throws Exception {
        try (var client = create(ICalculator.class)) {

            assertEquals("C#", client.getName());

            var value1 = 100.00D;
            var value2 = 15.99D;
            var result = client.add(value1, value2);
            assertEquals(115.99, result);

            value1 = 145.00D;
            value2 = 76.54D;
            result = client.subtract(value1, value2);
            assertEquals(68.46, result);

            value1 = 9.00D;
            value2 = 81.25D;
            result = client.multiply(value1, value2);
            assertEquals(731.25, result);

            value1 = 22.00D;
            value2 = 7.00D;
            result = client.divide(value1, value2);
            assertEquals(3.142857142857143, result);
        }
    }
}
