package org.example.api;

public interface IDataTyped {

    String ToSimpleText(byte y, short s, int i, long l, float f, double d,
                        boolean b, char c, String t);

    String ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f, double[] d,
                       boolean[] b, char[] c, String[] t);
}