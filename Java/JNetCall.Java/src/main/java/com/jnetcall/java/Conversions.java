package com.jnetcall.java;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Conversions {
    private static final Base64.Decoder _decoder = Base64.getDecoder();

    public static Object[] convert(Class<?>[] types, Object[] values) {
        for (var i = 0; i < types.length && i < values.length; i++) {
            var value = values[i];
            if (value == null)
                continue;
            var type = types[i];
            if (type.isAssignableFrom(value.getClass()))
                continue;
            values[i] = convert(type, value);
        }
        return values;
    }

    private static Object convert(Class<?> type, Object value) {
        if (type == String.class) {
            return value.toString();
        }
        if (type == byte.class) {
            return ((Number) value).byteValue();
        }
        if (type == short.class) {
            return ((Number) value).shortValue();
        }
        if (type == int.class) {
            return ((Number) value).intValue();
        }
        if (type == long.class) {
            return ((Number) value).longValue();
        }
        if (type == float.class) {
            return ((Number) value).floatValue();
        }
        if (type == double.class) {
            return ((Number) value).doubleValue();
        }
        if (type == boolean.class) {
            return ((Boolean) value).booleanValue();
        }
        if (type == char.class) {
            return ((String) value).charAt(0);
        }
        if (type == byte[].class) {
            return _decoder.decode(((String) value));
        }
        if (type == short[].class) {
            var list = (List) value;
            var array = new short[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (short) convert(short.class, list.get(i));
            return array;
        }
        if (type == int[].class) {
            var list = (List) value;
            var array = new int[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (int) convert(int.class, list.get(i));
            return array;
        }
        if (type == long[].class) {
            var list = (List) value;
            var array = new long[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (long) convert(long.class, list.get(i));
            return array;
        }
        if (type == float[].class) {
            var list = (List) value;
            var array = new float[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (float) convert(float.class, list.get(i));
            return array;
        }
        if (type == double[].class) {
            var list = (List) value;
            var array = new double[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (double) convert(double.class, list.get(i));
            return array;
        }
        if (type == boolean[].class) {
            var list = (List) value;
            var array = new boolean[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (boolean) convert(boolean.class, list.get(i));
            return array;
        }
        if (type == char[].class) {
            var list = (List) value;
            var array = new char[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (char) convert(char.class, list.get(i));
            return array;
        }
        if (type == String[].class) {
            var list = (List) value;
            var array = new String[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (String) convert(String.class, list.get(i));
            return array;
        }
        if (type == Set.class) {
            var list = (List) value;
            var set = new TreeSet();
            for (var item : list)
                set.add(item);
            return set;
        }
        if (type == ZonedDateTime.class){
            var txt = (String)value;
            var date = ZonedDateTime.parse(txt);
            return date;
        }
        if (type == LocalDateTime.class){
            var txt = (String)value;
            var date = LocalDateTime.parse(txt);
            return date;
        }
        if (type == Duration.class){
            var txt = (String)value;
            var tmp=txt.split(":");
            txt = "PT"+tmp[0]+"H"+tmp[1]+"M"+tmp[2]+"S";
            var date = Duration.parse(txt);
            return date;
        }
        var debug = type + " / " + value + " / " + value.getClass();
        throw new IllegalArgumentException(debug);
    }
}