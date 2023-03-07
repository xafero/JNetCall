package com.xafero.javaenums;

import java.util.ArrayList;
import java.util.List;

import com.xafero.javaenums.bits.BitFlag16;
import com.xafero.javaenums.bits.BitFlag32;
import com.xafero.javaenums.bits.BitFlag64;
import com.xafero.javaenums.bits.BitFlag8;
import com.xafero.javaenums.flags.ByteFlag;
import com.xafero.javaenums.flags.IntFlag;
import com.xafero.javaenums.flags.LongFlag;
import com.xafero.javaenums.flags.ShortFlag;
import com.xafero.javaenums.units.ByteEnum;
import com.xafero.javaenums.units.IntEnum;
import com.xafero.javaenums.units.LongEnum;
import com.xafero.javaenums.units.ShortEnum;

public final class Enums {

    public static Object castToEnum(Object value, Class<?> type) {
        String kind = type.getName();
        if (type.isEnum()) {
            Object[] items = type.getEnumConstants();
            Object first = items[0];
            if (first instanceof LongFlag) {
            	LongFlag l = (LongFlag)first;
                return fromLong((Class) l.getClass(), (long) value);
            } else if (first instanceof IntFlag) {
            	IntFlag i = (IntFlag)first;
                return fromInt((Class) i.getClass(), (int) value);
            } else if (first instanceof ShortFlag) {
            	ShortFlag s = (ShortFlag)first;
                return fromShort((Class) s.getClass(), (short) value);
            } else if (first instanceof ByteFlag) {
            	ByteFlag s = (ByteFlag)first;
                return fromByte((Class) s.getClass(), (byte) value);
            }
            for (Object item : items)
                if (getOrdinal(item).equals(value))
                    return item;
        }
        throw new IllegalArgumentException(value + " as " + kind);
    }

    public static Object castToNumber(Object value, Class<?> type) {
        Number num = getOrdinal(value);
        String kind = type.getName();
        switch (kind) {
            case "long": return num.longValue();
            case "int": return num.intValue();
            case "short": return num.shortValue();
            case "byte": return num.byteValue();
            default: throw new IllegalArgumentException(value + " (" + num + ") as " + kind);
        }
    }

    public static Number getOrdinal(Object value) {
        if (value instanceof LongEnum) {
        	LongEnum l = (LongEnum)value;
            return l.asNumber();
        }
        if (value instanceof IntEnum) {
        	IntEnum i = (IntEnum)value;
            return i.asNumber();
        }
        if (value instanceof ShortEnum) {
        	ShortEnum s = (ShortEnum)value;
            return s.asNumber();
        }
        if (value instanceof ByteEnum) {
        	ByteEnum b = (ByteEnum)value;
            return b.asNumber();
        }
        Enum tmp = (Enum) value;
        return tmp.ordinal();
    }

    public static Class getEnumUnderlyingType(Class clazz) {
        return ByteEnum.class.isAssignableFrom(clazz) ? byte.class
                : ShortEnum.class.isAssignableFrom(clazz) ? short.class
                : LongEnum.class.isAssignableFrom(clazz) ? long.class
                : int.class;
    }

    public static <T extends Enum & LongFlag> BitFlag64<T> fromLong(Class<T> clazz, long value) {
        List list = new ArrayList<T>();
        for (T item : clazz.getEnumConstants())
            if ((value & item.asNumber()) != 0)
                list.add(item);
        return BitFlag.of64(clazz, list);
    }

    public static <T extends Enum & IntFlag> BitFlag32<T> fromInt(Class<T> clazz, int value) {
    	List list = new ArrayList<T>();
        for (T item : clazz.getEnumConstants())
            if ((value & item.asNumber()) != 0)
                list.add(item);
        return BitFlag.of32(clazz, list);
    }

    public static <T extends Enum & ShortFlag> BitFlag16<T> fromShort(Class<T> clazz, short value) {
    	List list = new ArrayList<T>();
        for (T item : clazz.getEnumConstants())
            if ((value & item.asNumber()) != 0)
                list.add(item);
        return BitFlag.of16(clazz, list);
    }

    public static <T extends Enum & ByteFlag> BitFlag8<T> fromByte(Class<T> clazz, byte value) {
        List<T> list = new ArrayList<T>();
        for (T item : clazz.getEnumConstants())
            if ((value & item.asNumber()) != 0)
                list.add(item);
        return BitFlag.of8(clazz, list);
    }

    public static <T extends Enum & LongFlag> long toLong(Iterable<T> enums) {
        long flag = 0;
        for (T item : enums)
            flag |= item.asNumber();
        return flag;
    }

    public static <T extends Enum & IntFlag> int toInt(Iterable<T> enums) {
        int flag = 0;
        for (T item : enums)
            flag |= item.asNumber();
        return flag;
    }

    public static <T extends Enum & ShortFlag> short toShort(Iterable<T> enums) {
        short flag = 0;
        for (T item : enums)
            flag |= item.asNumber();
        return flag;
    }

    public static <T extends Enum & ByteFlag> byte toByte(Iterable<T> enums) {
        byte flag = 0;
        for (T item : enums)
            flag |= item.asNumber();
        return flag;
    }

    public static <T extends Enum> T notNull(Class<T> clazz, T maybe) {
        if (maybe != null) {
            return maybe;
        }
        T[] values = clazz.getEnumConstants();
        T defaultVal = values[0];
        return defaultVal;
    }

    public static boolean isEnum(Class<?> type)
    {
        return type.isEnum() || BitFlag.class.isAssignableFrom(type);
    }
}
