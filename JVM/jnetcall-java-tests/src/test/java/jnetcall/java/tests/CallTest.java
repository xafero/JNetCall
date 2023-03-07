package jnetcall.java.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.example.api.ICalculator;
import org.example.api.IDataTyped;
import org.example.api.IMultiple;
import org.example.api.IMultiple.Days;
import org.example.api.IMultiple.WeekDay;
import org.example.api.ISimultaneous;
import org.example.api.IStringCache;
import org.example.api.ITriggering;
import org.example.api.ITriggering.PCallBack;
import org.example.api.ITriggering.ThresholdHandler;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.google.common.base.Stopwatch;
import com.xafero.javaenums.BitFlag;

import jnetbase.java.sys.Primitives;
import jnetbase.java.threads.Tasks;

public abstract class CallTest {

    protected abstract <T extends AutoCloseable> T create(Class<T> clazz);

    protected String patch(String input) { return input; }

    protected int getMaxListWait() { return 1; }

    @Test
    public void shouldCallCache() throws Exception {
        String[] input = new String[]{"life", "on", "mars"};
        try (IStringCache client = create(IStringCache.class)) {

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

            assertThrows(UnsupportedOperationException.class, () -> client.get(89));
            client.clean();
        }
    }

    @Test
    public void shouldCallMultiple() throws Exception {
        try (IMultiple client = create(IMultiple.class)) {

            Pair<Integer, String> t2T = client.GetTuple2T(200, "Greece");
            Pair<Integer, String> t2V = client.GetTuple2V(t2T);
            assertEquals(t2T, t2V);

            Triplet<Integer, String, Boolean> t3T = client.GetTuple3T(1, "cat", true);
            Triplet<Integer, String, Boolean> t3V = client.GetTuple3V(t3T);
            assertEquals(t3T, t3V);

            Quartet<String, String[], Integer, int[]> t4T = client.GetTuple4T("perl", new String[]{"java", "c#"}, 1, new int[]{2, 3});
            Quartet<String, String[], Integer, int[]> t4V = client.GetTuple4V(t4T);
            assertEquals(t4T.getValue0(), t4V.getValue0());
            assertEquals(t4T.getValue1(), t4V.getValue1());
            assertEquals(t4T.getValue2(), t4V.getValue2());
            assertEquals(t4T.getValue3(), t4V.getValue3());

            Quintet<Integer, Float, Long, String, String> t5T = client.GetTuple5T(1, 1.5f, 2L, "dot", "net");
            Quintet<Integer, Float, Long, String, String> t5V = client.GetTuple5V(t5T);
            assertEquals(t5T, t5V);

            WeekDay bd1 = client.FindBestDay(3);
            assertEquals("Wednesday", bd1.toString());
            WeekDay bd2 = client.FindBestDay(5);
            assertEquals("Friday", bd2.toString());
            BitFlag<Days> bds = client.FindFreeDays();
            assertEquals("[Sunday, Thursday, Saturday]", bds.toString());

            String bdt = client.GetTextOf(new IMultiple.WeekDay[]{bd1, bd2}, bds);
            assertEquals("[Wednesday, Friday] | [Sunday, Thursday, Saturday]", bdt);
        }
    }

    @Test
    public void shouldCallDataTyped() throws Exception {
        try (IDataTyped client = create(IDataTyped.class)) {

            LocalDateTime now = LocalDateTime.now();
            Duration dur = Duration.ofSeconds(94);
            HashMap<String, Integer> env = new HashMap<String, Integer>();
            env.put("Caller", "Program".length());
            String[] dict = client.GetSystemVariables(now, dur, env)
                    .entrySet().stream().map(f -> '['+f.getKey()+", "+f.getValue()+']').toArray(String[]::new);
            assertEquals("[Caller, 7]|[seconds, 94]|[year, 2023]", Strings.join("|", dict));

            List<String> lines = Arrays.asList(new String[]{"Dog  ", "Hot", "Dog ", "Dog", "Hot    ", "Cat", "Cat", "Hot", "Hot"});
            int lineCount = client.GetLineCount(lines.toArray(String[]::new));
            Set<String> set = client.GetUnique(lines, true);
            assertEquals("Cat|Dog|Hot", Strings.join("|", set.stream().toArray(String[]::new)));
            assertEquals(9, lineCount);

            String[] list = client.GetDouble(set).toArray(String[]::new);
            assertEquals("Cat|Dog|Hot|Cat|Dog|Hot", Strings.join("|", list));

            String[] fs = Arrays.stream(Primitives.castInt(client.AllocateBytes(3, (byte) 42)))
                    .boxed().map(f -> f.toString()).toArray(String[]::new);
            assertEquals("42|42|42", Strings.join("|", fs));

            long bsf = client.GetFileSize("Z:\\Nothing\\Good\\No fun with that file.txt");
            assertEquals(41, bsf);

            byte ay = Byte.MAX_VALUE; short as = Short.MAX_VALUE; int ai = Integer.MAX_VALUE;
            long al = Long.MAX_VALUE; float af = Float.MAX_VALUE; double ad = Double.MAX_VALUE;
            boolean ab = true; char ac = Character.MAX_VALUE; String at = "Str";
            BigDecimal au = BigDecimal.valueOf(Long.MAX_VALUE);
            UUID ag = UUID.fromString("27edb110-afef-4ce3-b8c1-3fcb2ec3fabe");
            String txt = client.ToSimpleText(ay, as, ai, al, af, ad, ab, ac, at, au, ag).replace(" ", "")
                    .trim().replace("3,4", "3.4").replace("1,7", "1.7");
            txt = patch(txt);
            assertEquals("y=127,s=32767,i=2147483647,l=9223372036854775807,f=3.4028235E+38,d=1.7976931348623157E+308,b=True,c=￿,t=Str,u=9223372036854775807,g=27edb110-afef-4ce3-b8c1-3fcb2ec3fabe", txt);

            byte[] by = new byte[]{ 42 }; short[] bs = new short[]{ Short.MIN_VALUE }; int[] bi = new int[]{ Integer.MIN_VALUE };
            long[] bl = new long[]{ Long.MIN_VALUE }; float[] bf = new float[]{ -3.4028235E38f };
            double[] bd = new double[]{ -1.7976931348623157E308d }; boolean[] bb = new boolean[]{ false };
            char[] bc = new char[]{ 'X' }; String[] bt = new String[]{ "Str1" };
            BigDecimal[] bu = new BigDecimal[] { BigDecimal.valueOf(Long.MIN_VALUE) };
            UUID[] bg = new UUID[]{ UUID.fromString("00000000-0000-0000-0000-000000000000") };
            txt = client.ToArrayText(by, bs, bi, bl, bf, bd, bb, bc, bt, bu, bg).replace(" ", "")
                    .trim().replace("3,4", "3.4").replace("1,7", "1.7");
            txt = patch(txt);
            assertEquals("y=[42],s=[-32768],i=[-2147483648],l=[-9223372036854775808],f=[-3.4028235E+38],d=[-1.7976931348623157E+308],b=[False],c=[X],t=[Str1],u=[-9223372036854775808],g=[00000000-0000-0000-0000-000000000000]", txt);
        }
    }

    @Test
    public void shouldCallSimultan() throws Exception {
        try (ISimultaneous client = create(ISimultaneous.class)) {

            client.loadIt("Hello").get();

            Integer id = client.getId().get();
            assertTrue(id >= -100 && id <= 100, id + " ?!");

            String txt = client.removeIt().get();
            assertEquals("Hello", txt);

            final int count = 3;
            IntStream range = IntStream.range(0, count);

            Stopwatch watch = Stopwatch.createStarted();
            List<CompletableFuture<Pair<Integer, Long>>> tasks = range.mapToObj(i -> client.runIt(26, i)).toList();
            watch.stop();
            long listTime = watch.elapsed(TimeUnit.MILLISECONDS);

            List<Pair<Integer, Long>> all = Tasks.whenAll(tasks);
            assertEquals(count, all.size());

            Object[] numbers = all.stream().map(t -> t.getValue0()).distinct().toArray();
            assertEquals(count, numbers.length);

            assertTrue(listTime >= 0 && listTime <= getMaxListWait(), listTime + " ?!");
        }
    }

    @Test
    public void shouldCallCalculator() throws Exception {
        try (ICalculator client = create(ICalculator.class)) {

            assertEquals("C#", client.getName());

            double value1 = 100.00D;
            double value2 = 15.99D;
            double result = client.add(value1, value2);
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

    @Test
    public void shouldCallTrigger() throws Exception {
        try (ITriggering client = create(ITriggering.class)) {

            final int cbCount = 3;
            ArrayList<Pair<Integer, String>> cbList = new ArrayList<Pair<Integer, String>>();
            final CountDownLatch clc = new CountDownLatch(cbCount);

            PCallBack EnumWindowsCallback = (handle, lParam) -> {
                cbList.add(Pair.with(handle, lParam));
                clc.countDown();
                return true;
            };

            boolean callOk = client.enumWindows(EnumWindowsCallback, cbCount);
            assertTrue(callOk);
            clc.await(5, TimeUnit.SECONDS);

            assertEquals(cbCount, cbList.size());
            assertEquals("[[0, 3!], [1, 4!], [2, 5!]]", cbList.toString());

            final int evtCount = 4;
            ArrayList<Pair<String, Integer>> evtList = new ArrayList<Pair<String, Integer>>();
            final CountDownLatch cle = new CountDownLatch(evtCount);

            ThresholdHandler OnThresholdReached = (sender, e) -> {
                String s = sender.toString();
                evtList.add(Pair.with(s, e.Threshold()));
                cle.countDown();
            };

            client.addThresholdReached(OnThresholdReached);
            client.startPub(evtCount);
            cle.await(5, TimeUnit.SECONDS);

            assertEquals(evtCount, evtList.size());
            client.removeThresholdReached(OnThresholdReached);
        }
    }
}
