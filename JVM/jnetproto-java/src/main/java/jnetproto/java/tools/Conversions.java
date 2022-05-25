package jnetproto.java.tools;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;

import java.lang.reflect.Executable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Conversions {

    public static Object[] convertFor(Object[] args, Executable method) {
        var genTypes = method.getGenericParameterTypes();
        return convert(genTypes, args);
    }

    public static Object[] convert(Type[] types, Object[] args) {
        for (var i = 0; i < args.length; i++)
            args[i] = convert(types[i], args[i]);
        return args;
    }

    public static Object convert(Type type, Object arg) {
        if (type instanceof ParameterizedType param) {
            var baseType = param.getRawType();
            var typeArgs = param.getActualTypeArguments();
            if (BitFlag.class.equals(baseType)) {
                return Enums.castToEnum(arg, (Class<?>) typeArgs[0]);
            }
        }
        if (type instanceof Class clazz) {
            if (clazz.isInstance(arg)) {
                return arg;
            }
            if (Enums.isEnum(clazz)) {
                return Enums.castToEnum(arg, clazz);
            }
        }
        return arg;
    }
}
