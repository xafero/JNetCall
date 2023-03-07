package com.xafero.javaenums.bits;

import java.util.Collection;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.ByteFlag;
import com.xafero.javaenums.units.ByteEnum;

public final class BitFlag8<T extends Enum & ByteFlag>
        extends BitFlag<T>
        implements ByteEnum {

    public BitFlag8(Class<T> enumType, Collection<T> items) {
        super(enumType, items);
    }

    @Override
    public Byte asNumber() {
        return toByte();
    }

    public byte toByte() {
        return Enums.toByte(_items);
    }
}
