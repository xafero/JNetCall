package jnetbase.java.sys;

public final class Primitives {

    public static int[] castInt(byte[] array) {
        int[] values = new int[array.length];
        for (int i = 0; i < values.length; i++)
            values[i] = array[i];
        return values;
    }
}
