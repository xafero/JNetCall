package jnetbase.java.sys;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class ArrayX {

    public static ArrayList<Object> asObjectList(Iterable items) {
    	ArrayList<Object> list = new ArrayList<Object>();
        for (Object item : items)
            list.add(item);
        return list;
    }

    public static Object[] asObjectArray(Object raw) {
        if (raw instanceof Iterable<?>)
            return asObjectArray((Iterable<?>)raw);
        Object[] array = new Object[Array.getLength(raw)];
        for (int i = 0; i < array.length; i++)
            array[i] = Array.get(raw, i);
        return array;
    }

    public static Object[] asObjectArray(Iterable items) {
    	ArrayList<Object> list = asObjectList(items);
    	Object[] array = new Object[list.size()];
        return list.toArray(array);
    }

    private static Object noConvert(Object obj, Type type) {
        return obj;
    }

    public static Object asTypedArray(Object[] args, Class<?> type, BiFunction<Object, Type, Object> convert) {
        convert = convert == null ? ArrayX::noConvert : convert;
        Object array = Array.newInstance(type, args.length);
        for (int i = 0; i < args.length; i++)
            Array.set(array, i, convert.apply(args[i], type));
        return array;
    }

    public static List<?> asTypedArrayList(Object[] args, Type type, BiFunction<Object, Type, Object> convert) {
        convert = convert == null ? ArrayX::noConvert : convert;
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object arg : args)
            list.add(convert.apply(arg, type));
        return list;
    }
}
