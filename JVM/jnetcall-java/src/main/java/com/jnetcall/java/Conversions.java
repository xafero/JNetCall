package com.jnetcall.java;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public final class Conversions {
    private static final Base64.Decoder _decoder = Base64.getDecoder();

    public static Object[] convert(Class<?>[] types, Object[] values, String[] kinds) {
        for (var i = 0; i < types.length && i < values.length; i++) {
            var value = values[i];
            if (value == null)
                continue;
            var kind = kinds[i];
            var type = types[i];
            if (type.isAssignableFrom(value.getClass()))
                continue;
            values[i] = convert(type, value, kind);
        }
        return values;
    }

    private static Object convert(Class<?> type, Object value, String kind) {
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
        if (type == BigDecimal.class) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (type == UUID.class) {
            return UUID.fromString((String) value);
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
            var list = (List<?>) value;
            var array = new short[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (short) convert(short.class, list.get(i), kind);
            return array;
        }
        if (type == int[].class) {
            var list = (List<?>) value;
            var array = new int[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (int) convert(int.class, list.get(i), kind);
            return array;
        }
        if (type == long[].class) {
            var list = (List<?>) value;
            var array = new long[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (long) convert(long.class, list.get(i), kind);
            return array;
        }
        if (type == float[].class) {
            var list = (List<?>) value;
            var array = new float[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (float) convert(float.class, list.get(i), kind);
            return array;
        }
        if (type == double[].class) {
            var list = (List<?>) value;
            var array = new double[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (double) convert(double.class, list.get(i), kind);
            return array;
        }
        if (type == BigDecimal[].class) {
            var list = (List<?>) value;
            var array = new BigDecimal[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (BigDecimal) convert(BigDecimal.class, list.get(i), kind);
            return array;
        }
        if (type == UUID[].class) {
            var list = (List<?>) value;
            var array = new UUID[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (UUID) convert(UUID.class, list.get(i), kind);
            return array;
        }
        if (type == boolean[].class) {
            var list = (List<?>) value;
            var array = new boolean[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (boolean) convert(boolean.class, list.get(i), kind);
            return array;
        }
        if (type == char[].class) {
            var list = (List<?>) value;
            var array = new char[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (char) convert(char.class, list.get(i), kind);
            return array;
        }
        if (type == String[].class) {
            var list = (List<?>) value;
            var array = new String[list.size()];
            for (var i = 0; i < array.length; i++)
                array[i] = (String) convert(String.class, list.get(i), kind);
            return array;
        }
        if (type == Set.class) {
            var list = (List<?>) value;
            var set = new TreeSet<Object>();
            for (var item : list)
                set.add(item);
            return set;
        }
        if (type == Pair.class) {
            var map = (Map<?,?>) value;
            var kinds= GetHintClasses(kind);
            var item1 = convert(kinds[0], map.get("Item1"), kind);
            var item2 = convert(kinds[1], map.get("Item2"), kind);
            return Pair.with(item1, item2);
        }
        if (type == Triplet.class) {
            var map = (Map<?,?>) value;
            var kinds= GetHintClasses(kind);
            var item1 = convert(kinds[0], map.get("Item1"), kind);
            var item2 = convert(kinds[1], map.get("Item2"), kind);
            var item3 = convert(kinds[2], map.get("Item3"), kind);
            return Triplet.with(item1, item2, item3);
        }
        if (type == Quartet.class){
            var map = (Map<?,?>) value;
            var kinds= GetHintClasses(kind);
            var item1 = convert(kinds[0], map.get("Item1"), kind);
            var item2 = convert(kinds[1], map.get("Item2"), kind);
            var item3 = convert(kinds[2], map.get("Item3"), kind);
            var item4 = convert(kinds[3], map.get("Item4"), kind);
            return Quartet.with(item1, item2, item3, item4);
        }
        if (type == ZonedDateTime.class) {
            var txt = (String) value;
            var date = ZonedDateTime.parse(txt);
            return date;
        }
        if (type == LocalDateTime.class) {
            var txt = (String) value;
            var date = LocalDateTime.parse(txt);
            return date;
        }
        if (type == Duration.class) {
            var txt = (String) value;
            var tmp = txt.split(":");
            txt = "PT" + tmp[0] + "H" + tmp[1] + "M" + tmp[2] + "S";
            var date = Duration.parse(txt);
            return date;
        }
        var debug = type + " / " + value + " / " + value.getClass();
        throw new IllegalArgumentException(debug);
    }

    private static Class<?>[] GetHintClasses(String hint) {
        var first = hint.split("<", 2)[1];
        var last = first.split(">", 2)[0];
        var parts = last.split(";");
        var types = new Class[parts.length];
        for (var i = 0; i < parts.length; i++)
            types[i] = GetHintClass(parts[i]);
        return types;
    }

    private static Class<?> GetHintClass(String typeName) {
        switch (typeName) {
            case "bool": return boolean.class;
            case "int32": return int.class;
            case "string": return String.class;
            case "string[]": return String[].class;
            default: throw new IllegalArgumentException(typeName);
        }
    }
}