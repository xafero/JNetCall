package jnetproto.java.tools;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import jnetbase.java.meta.Reflect;
import jnetproto.java.api.DataType;
import jnetproto.java.core.DataTypes;

import java.lang.reflect.*;
import java.util.Arrays;

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
            if (clazz.isArray()) {
                var arrayType = clazz.getComponentType();
                if (Enums.isEnum(arrayType)) {
                    var arraySize = Array.getLength(arg);
                    var array = Array.newInstance(arrayType, arraySize);
                    for (var i = 0; i < arraySize; i++) {
                        var item = Array.get(arg, i);
                        var conv = Enums.castToEnum(item, arrayType);
                        Array.set(array, i, conv);
                    }
                    return array;
                }
            }
            if (Enums.isEnum(clazz)) {
                return Enums.castToEnum(arg, clazz);
            }
        }
        return arg;
    }

    private static boolean isUsable(Constructor creator, Object[] args) {
        return creator.getParameters().length == args.length;
    }

    public static Object fromObjectArray(Class<?> type, Object[] args) {
        try {
            var cTypes = Arrays.stream(args).map(DataTypes::toClass).toArray(Class[]::new);
            var creator = Reflect.getConstructor(type, cTypes);
            if (creator == null || !isUsable(creator, args)) {
                creator = Arrays.stream(type.getConstructors()).findFirst().orElse(null);
                if (creator == null || !isUsable(creator, args)) {
                    var props = Reflect.getProperties(type);
                    if (props.size() == args.length) {
                        var obj = Reflect.createNew(type);
                        for (var i = 0; i < props.size(); i++) {
                            var arg = args[i];
                            var prop = props.get(i);
                            prop.Set().invoke(obj, arg);
                        }
                        return obj;
                    }
                    throw new IllegalArgumentException("No constructor: " + type);
                }
            }
            args = Conversions.convertFor(args, creator);
            return creator.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object toObjectArray(Object obj) {
        try {
            var kind = DataTypes.getKind(obj);
            if (kind.Kind() != DataType.Unknown) {
                if (kind instanceof DataTypes.ArrayDt at && at.Item().Kind() == DataType.Unknown)
                    obj = ArrayX.asObjectArray(obj);
                if (kind instanceof DataTypes.ListDt lt && lt.Item().Kind() == DataType.Unknown)
                    obj = ArrayX.asObjectArray((Iterable)obj);
                if (obj instanceof Object[] ar && Arrays.stream(ar)
                        .anyMatch(a -> DataTypes.getKind(a).Kind() == DataType.Unknown))
                    for (var i = 0; i < ar.length; i++)
                        ar[i] = toObjectArray(ar[i]);
                return obj;
            }
            var type = obj.getClass();
            var props = Reflect.getProperties(type);
            var args = new Object[props.size()];
            for (var i = 0; i < args.length; i++) {
                var prop = props.get(i);
                var propGet = prop.Get();
                var propVal = propGet.invoke(obj);
                args[i] = toObjectArray(propVal);
            }
            return args;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
