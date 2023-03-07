package com.xafero.javaenums.bits;

import java.util.Collection;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.IntFlag;
import com.xafero.javaenums.units.IntEnum;

public final class BitFlag32<T extends Enum & IntFlag>
        extends BitFlag<T>
        implements IntEnum {

    public BitFlag32(Class<T> enumType, Collection<T> items) {
        super(enumType, items);
    }

    @Override
    public Integer asNumber() {
        return toInt();
    }

    public int toInt() {
        return Enums.toInt(_items);
    }
}
