package com.xafero.javaenums.bits;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.LongFlag;
import com.xafero.javaenums.units.LongEnum;

import java.util.Collection;

public final class BitFlag64<T extends Enum & LongFlag>
        extends BitFlag<T>
        implements LongEnum {

    public BitFlag64(Class<T> enumType, Collection<T> items) {
        super(enumType, items);
    }

    @Override
    public Long asNumber() {
        return toLong();
    }

    public long toLong() {
        return Enums.toLong(_items);
    }
}
