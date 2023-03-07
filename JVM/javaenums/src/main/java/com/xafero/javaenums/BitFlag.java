package com.xafero.javaenums;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.xafero.javaenums.api.IFlag;
import com.xafero.javaenums.bits.BitFlag16;
import com.xafero.javaenums.bits.BitFlag32;
import com.xafero.javaenums.bits.BitFlag64;
import com.xafero.javaenums.bits.BitFlag8;
import com.xafero.javaenums.flags.ByteFlag;
import com.xafero.javaenums.flags.IntFlag;
import com.xafero.javaenums.flags.LongFlag;
import com.xafero.javaenums.flags.ShortFlag;

public abstract class BitFlag<T extends Enum & IFlag> {
    protected final Class<T> _enumType;
    protected final Set<T> _items;

    protected BitFlag(Class<T> enumType, Collection<T> items) {
        _enumType = enumType;
        _items = new TreeSet<>(items);
    }

    public Class<T> getEnumType() {
        return _enumType;
    }

    @Override
    public String toString() {
        return _items.toString();
    }

    public T[] toArray() {
        T[] array = (T[]) Array.newInstance(_enumType, _items.size());
        return _items.toArray(array);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BitFlag<?> bitFlag = (BitFlag<?>) o;
        return _enumType.equals(bitFlag._enumType) && _items.equals(bitFlag._items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_enumType, _items);
    }

    public static <I extends Enum & LongFlag> BitFlag64<I> of64(Class<I> clazz, I... items) {
        return items == null ? of64(clazz) : of64(clazz, Arrays.asList(items));
    }

    public static <I extends Enum & IntFlag> BitFlag32<I> of32(Class<I> clazz, I... items) {
        return items == null ? of32(clazz) : of32(clazz, Arrays.asList(items));
    }

    public static <I extends Enum & ShortFlag> BitFlag16<I> of16(Class<I> clazz, I... items) {
        return items == null ? of16(clazz) : of16(clazz, Arrays.asList(items));
    }

    public static <I extends Enum & ByteFlag> BitFlag8<I> of8(Class<I> clazz, I... items) {
        return items == null ? of8(clazz) : of8(clazz, Arrays.asList(items));
    }

    public static <I extends Enum & LongFlag> BitFlag64<I> of64(Class<I> clazz, Collection<I> coll) {
        return new BitFlag64(clazz, coll);
    }

    public static <I extends Enum & IntFlag> BitFlag32<I> of32(Class<I> clazz, Collection<I> coll) {
        return new BitFlag32(clazz, coll);
    }

    public static <I extends Enum & ShortFlag> BitFlag16<I> of16(Class<I> clazz, Collection<I> coll) {
        return new BitFlag16(clazz, coll);
    }

    public static <I extends Enum & ByteFlag> BitFlag8<I> of8(Class<I> clazz, Collection<I> coll) {
        return new BitFlag8(clazz, coll);
    }
}
