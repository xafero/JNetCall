package com.xafero.javaenums.bits;

import java.util.Collection;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.ShortFlag;
import com.xafero.javaenums.units.ShortEnum;

public final class BitFlag16<T extends Enum & ShortFlag>
        extends BitFlag<T>
        implements ShortEnum {

    public BitFlag16(Class<T> enumType, Collection<T> items) {
        super(enumType, items);
    }

    @Override
    public Short asNumber() {
        return toShort();
    }

    public short toShort() {
        return Enums.toShort(_items);
    }
}
