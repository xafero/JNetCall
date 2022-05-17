package org.example.impl;

import org.example.api.ICalculator;
import org.example.api.IDataTyped;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class CalculatorService implements ICalculator, IDataTyped {

    public double Add(double n1, double n2) {
        double result = n1 + n2;
        System.err.printf("Received Add(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Subtract(double n1, double n2) {
        double result = n1 - n2;
        System.err.printf("Received Subtract(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Multiply(double n1, double n2) {
        double result = n1 * n2;
        System.err.printf("Received Multiply(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Divide(double n1, double n2) {
        double result = n1 / n2;
        System.err.printf("Received Divide(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
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
    public Map<String, Integer> GetSystemVariables(ZonedDateTime dts, Duration dur,
                                                   Map<String, Integer> parent) {
        var map = new LinkedHashMap<>(parent);
        map.put("year", dts.getYear());
        map.put("seconds", (int) dur.getSeconds());
        return map;
    }
}