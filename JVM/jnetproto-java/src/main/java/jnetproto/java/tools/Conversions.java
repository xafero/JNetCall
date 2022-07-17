package jnetproto.java.tools;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import jnetbase.java.meta.Reflect;
import jnetbase.java.sys.ArrayX;
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

    public static Object fromObjectArray(Type type, Object[] args) {
        try {
            var kind = DataTypes.getKind(type);
            if (kind.Kind() != DataType.Unknown)
            {
                if (kind instanceof DataTypes.ArrayDt at && at.Item().Kind() == DataType.Unknown)
                {
                    var arrayType = ((Class<?>)type).getComponentType();
                    var array = ArrayX.asTypedArray(args, arrayType, Conversions::convertRaw);
                    return array;
                }
                if (kind instanceof DataTypes.ListDt lt && lt.Item().Kind() == DataType.Unknown)
                {
                    var listType = ((ParameterizedType)type).getActualTypeArguments()[0];
                    var list = ArrayX.asTypedArrayList(args, listType, Conversions::convertRaw);
                    return list;
                }
            }
            var inputTypes = Arrays.stream(args).map(DataTypes::toClass).toArray(Class[]::new);
            var creator = Reflect.getConstructor(type, inputTypes);
            if (creator == null || !isUsable(creator, args)) {
                creator = Reflect.getFirstConstructor(type);
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
            var outputTypes = creator.getParameters();
            args = Conversions.convertFor(args, creator);
            args = convertFor(args, outputTypes);
            return creator.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[] convertFor(Object[] args, Parameter[] parameters)
    {
        for (var i = 0; i < args.length && i < parameters.length; i++)
        {
            var prm = parameters[i].getParameterizedType();
            var arg = args[i];
            var value = convertRaw(arg, prm);
            args[i] = value;
        }
        return args;
    }

    private static Object convertRaw(Object v, Type r)
    {
        var t = r instanceof Class<?> c ? c : (Class) ((ParameterizedType)r).getRawType();
        if (t.isEnum())
            return v;
        if (t.equals(Object.class))
            return v;
        if (t.isInstance(v))
            return v;
        if (v instanceof Object[] va)
            return fromObjectArray(r, va);
        return v;
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
