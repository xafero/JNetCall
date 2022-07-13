package jnetbase.java.sys;

public final class Primitives {

    public static int[] castInt(byte[] array) {
        var values = new int[array.length];
        for (var i = 0; i < values.length; i++)
            values[i] = array[i];
        return values;
    }
}
