package org.example.impl;

import org.example.api.ICalculator;
import org.example.api.IDataTyped;

import java.util.Arrays;

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
                               double d, boolean b, char c, String t) {
        var bld = new StringBuilder();
        bld.append(" y=" + y);
        bld.append(" s=" + s);
        bld.append(" i=" + i);
        bld.append(" l=" + l);
        bld.append(" f=" + f);
        bld.append(" d=" + d);
        bld.append(" b=" + b);
        bld.append(" c=" + c);
        bld.append(" t=" + t);
        return bld.toString();
    }

    @Override
    public String ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f,
                              double[] d, boolean[] b, char[] c, String[] t) {
        var bld = new StringBuilder();
        bld.append(" y=" + Arrays.toString(y));
        bld.append(" s=" + Arrays.toString(s));
        bld.append(" i=" + Arrays.toString(i));
        bld.append(" l=" + Arrays.toString(l));
        bld.append(" f=" + Arrays.toString(f));
        bld.append(" d=" + Arrays.toString(d));
        bld.append(" b=" + Arrays.toString(b));
        bld.append(" c=" + Arrays.toString(c));
        bld.append(" t=" + Arrays.toString(t));
        return bld.toString();
    }
}