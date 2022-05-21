package jnetproto.java.compat;

import jnetproto.java.DataType;
import jnetproto.java.DataTypes;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public final class Reflect {

    public static int getRank(Object value) {
        return getRank(value.getClass());
    }

    public static int getRank(Class type) {
        if (!type.isArray())
            return 0;
        return StringUtils.countMatches(type.getName(), '[');
    }

    public static byte getByte(DataTypes.IDataType kind) {
        return (byte) kind.Kind().ordinal();
    }

    public static DataType toDataType(int read) {
        return DataType.values()[read];
    }

    public static Object invoke(Method method, Object obj, Object[] args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Object obj, String name, Class<?>... args) {
        try {
            var clazz = obj.getClass();
            return clazz.getMethod(name, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
