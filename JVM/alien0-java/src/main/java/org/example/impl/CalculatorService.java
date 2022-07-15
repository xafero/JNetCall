package org.example.impl;

import com.xafero.javaenums.BitFlag;
import org.example.api.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CalculatorService implements ICalculator, IDataTyped, IMultiple, IStringCache,
        ISimultaneous, AutoCloseable, ITriggering {

    public double add(double n1, double n2) {
        double result = n1 + n2;
        return result;
    }

    public double subtract(double n1, double n2) {
        double result = n1 - n2;
        return result;
    }

    public double multiply(double n1, double n2) {
        double result = n1 * n2;
        return result;
    }

    public double divide(double n1, double n2) {
        double result = n1 / n2;
        return result;
    }

    @Override
    public String ToSimpleText(byte y, short s, int i, long l, float f,
                               double d, boolean b, char c, String t,
                               BigDecimal u, UUID g) {
        var bld = new StringBuilder();
        bld.append(" y = " + y);
        bld.append(", s = " + s);
        bld.append(", i = " + i);
        bld.append(", l = " + l);
        bld.append(", f = " + f);
        bld.append(", d = " + d);
        bld.append(", b = " + b);
        bld.append(", c = " + c);
        bld.append(", t = " + t);
        bld.append(", u = " + u);
        bld.append(", g = " + g);
        return bld.toString();
    }

    @Override
    public String ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f,
                              double[] d, boolean[] b, char[] c, String[] t,
                              BigDecimal[] u, UUID[] g) {
        var bld = new StringBuilder();
        bld.append(" y = " + Arrays.toString(y));
        bld.append(", s = " + Arrays.toString(s));
        bld.append(", i = " + Arrays.toString(i));
        bld.append(", l = " + Arrays.toString(l));
        bld.append(", f = " + Arrays.toString(f));
        bld.append(", d = " + Arrays.toString(d));
        bld.append(", b = " + Arrays.toString(b));
        bld.append(", c = " + Arrays.toString(c));
        bld.append(", t = " + Arrays.toString(t));
        bld.append(", u = " + Arrays.toString(u));
        bld.append(", g = " + Arrays.toString(g));
        return bld.toString();
    }

    @Override
    public int GetLineCount(String[] lines) {
        return lines.length;
    }

    @Override
    public long GetFileSize(String path) {
        return path.length();
    }

    @Override
    public byte[] AllocateBytes(int size, byte value) {
        var array = new byte[size];
        for (var i = 0; i < array.length; i++)
            array[i] = value;
        return array;
    }

    @Override
    public Set<String> GetUnique(List<String> lines, boolean withTrim) {
        var set = new TreeSet<String>();
        for (var line : lines)
            set.add(withTrim ? line.trim() : line);
        return set;
    }

    @Override
    public List<String> GetDouble(Set<String> lines) {
        var list = new LinkedList<String>();
        for (var line : lines)
            list.add(line);
        for (var line : lines)
            list.add(line);
        return list;
    }

    @Override
    public Map<String, Integer> GetSystemVariables(LocalDateTime dts, Duration dur,
                                                   Map<String, Integer> parent) {
        var map = new LinkedHashMap<>(parent);
        map.put("year", dts.getYear());
        map.put("seconds", (int) dur.getSeconds());
        return map;
    }

    @Override
    public CompletableFuture<Integer> getId() {
        return CompletableFuture.supplyAsync(() -> new Random().nextInt(-100, 100));
    }

    private String currentWord;

    @Override
    public CompletableFuture<Void> loadIt(String word) {
        currentWord = word;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> removeIt() {
        var res = CompletableFuture.completedFuture(currentWord);
        currentWord = null;
        return res;
    }

    @Override
    public CompletableFuture<Pair<Integer, Long>> runIt(int waitMs, int idx) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(waitMs);
                var current = Thread.currentThread().getId();
                return Pair.with(idx, current);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Pair<Integer, String> GetTuple2T(int a, String b) {
        return Pair.with(a, b);
    }

    @Override
    public Pair<Integer, String> GetTuple2V(Pair<Integer, String> v) {
        return Pair.with(v.getValue0(), v.getValue1());
    }

    @Override
    public Triplet<Integer, String, Boolean> GetTuple3T(int a, String b, boolean c) {
        return Triplet.with(a, b, c);
    }

    @Override
    public Triplet<Integer, String, Boolean> GetTuple3V(Triplet<Integer, String, Boolean> v) {
        return Triplet.with(v.getValue0(), v.getValue1(), v.getValue2());
    }

    @Override
    public Quartet<String, String[], Integer, int[]> GetTuple4T(String a, String[] b, int c, int[] d) {
        return Quartet.with(a, b, c, d);
    }

    @Override
    public Quartet<String, String[], Integer, int[]> GetTuple4V(Quartet<String, String[], Integer, int[]> v) {
        return Quartet.with(v.getValue0(), v.getValue1(), v.getValue2(), v.getValue3());
    }

    @Override
    public Quintet<Integer, Float, Long, String, String> GetTuple5T(int a, float b, long c, String d, String e) {
        return Quintet.with(a, b, c, d, e);
    }

    @Override
    public Quintet<Integer, Float, Long, String, String> GetTuple5V(Quintet<Integer, Float, Long, String, String> v) {
        return Quintet.with(v.getValue0(), v.getValue1(), v.getValue2(), v.getValue3(), v.getValue4());
    }

    @Override
    public WeekDay FindBestDay(int value) {
        if (value == WeekDay.Wednesday.Value) return WeekDay.Wednesday;
        if (value == WeekDay.Friday.Value) return WeekDay.Friday;
        throw new IllegalArgumentException(value + " !");
    }

    @Override
    public BitFlag<Days> FindFreeDays() {
        return BitFlag.of32(Days.class, Days.Saturday, Days.Thursday, Days.Sunday);
    }

    @Override
    public String GetTextOf(WeekDay[] taken, BitFlag<Days> days) {
        var bld = new StringBuilder();
        bld.append(Arrays.toString(taken));
        bld.append(" | ");
        bld.append(Arrays.toString(days.toArray()));
        return bld.toString();
    }

    private final Map<Integer, String> cache = new LinkedHashMap<>();

    @Override
    public void set(int key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(int key) throws UnsupportedOperationException {
        var value = cache.get(key);
        if (value == null)
            throw new UnsupportedOperationException(key + " ?!");
        return value;
    }

    @Override
    public void delete(int key) {
        cache.remove(key);
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean enumWindows(PCallBack callback, int count) {
        for (var i = 0; i < count; i++)
            callback.invoke(i, i + count + "!");
        return true;
    }

    @Override
    public void startPub(int count) {
        for (var i = 0; i < count; i++) {
            var arg = new ThresholdEventArgs(i, LocalDateTime.now());
            fireThresholdReached(this, arg);
        }
    }

    private final List<ThresholdHandler> ThresholdReached = new LinkedList<>();

    private void fireThresholdReached(Object sender, ThresholdEventArgs arg) {
        for (var handler : ThresholdReached)
            handler.invoke(sender, arg);
    }

    @Override
    public void addThresholdReached(ThresholdHandler value) {
        ThresholdReached.add(value);
    }

    @Override
    public void removeThresholdReached(ThresholdHandler value) {
        ThresholdReached.remove(value);        
    }

    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public void dispose() {
        close();
    }
}
