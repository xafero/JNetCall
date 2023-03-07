package jnetproto.java.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;

import jnetbase.java.meta.Property;
import jnetbase.java.meta.Reflect;
import jnetbase.java.sys.ArrayX;
import jnetproto.java.api.DataType;
import jnetproto.java.core.DataTypes;
import jnetproto.java.core.DataTypes.IDataType;

public final class Conversions {

    public static Object[] convertFor(Object[] args, Executable method) {
        Type[] genTypes = method.getGenericParameterTypes();
        return convert(genTypes, args);
    }

    public static Object[] convert(Type[] types, Object[] args) {
        for (int i = 0; i < args.length; i++)
            args[i] = convert(types[i], args[i]);
        return args;
    }

    public static Object convert(Type type, Object arg) {
        if (type instanceof ParameterizedType) {
        	ParameterizedType param = (ParameterizedType)type;
            Type baseType = param.getRawType();
            Type[] typeArgs = param.getActualTypeArguments();
            if (BitFlag.class.equals(baseType)) {
                return Enums.castToEnum(arg, (Class<?>) typeArgs[0]);
            }
        }
        if (type instanceof Class) {
        	Class clazz = (Class)type;
            if (clazz.isInstance(arg)) {
                return arg;
            }
            if (clazz.isArray()) {
                Class arrayType = clazz.getComponentType();
                if (Enums.isEnum(arrayType)) {
                    int arraySize = Array.getLength(arg);
                    Object array = Array.newInstance(arrayType, arraySize);
                    for (int i = 0; i < arraySize; i++) {
                        Object item = Array.get(arg, i);
                        Object conv = Enums.castToEnum(item, arrayType);
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
            IDataType kind = DataTypes.getKind(type);
            if (kind.Kind() != DataType.Unknown)
            {
                if (kind instanceof DataTypes.ArrayDt && ((DataTypes.ArrayDt)kind).Item().Kind() == DataType.Unknown)
                {
                    Class<?> arrayType = ((Class<?>)type).getComponentType();
                    Object array = ArrayX.asTypedArray(args, arrayType, Conversions::convertRaw);
                    return array;
                }
                if (kind instanceof DataTypes.ListDt && ((DataTypes.ListDt)kind).Item().Kind() == DataType.Unknown)
                {
                    Type listType = ((ParameterizedType)type).getActualTypeArguments()[0];
                    List<?> list = ArrayX.asTypedArrayList(args, listType, Conversions::convertRaw);
                    return list;
                }
            }
            Class[] inputTypes = Arrays.stream(args).map(DataTypes::toClass).toArray(Class[]::new);
            Constructor<Object> creator = Reflect.getConstructor(type, inputTypes);
            if (creator == null || !isUsable(creator, args)) {
                creator = Reflect.getFirstConstructor(type);
                if (creator == null || !isUsable(creator, args)) {
                    List<Property> props = Reflect.getProperties(type);
                    if (props.size() == args.length) {
                        Object obj = Reflect.createNew(type);
                        for (int i = 0; i < props.size(); i++) {
                            Object arg = args[i];
                            Property prop = props.get(i);
                            prop.Set().invoke(obj, arg);
                        }
                        return obj;
                    }
                    throw new IllegalArgumentException("No constructor: " + type);
                }
            }
            Parameter[] outputTypes = creator.getParameters();
            args = Conversions.convertFor(args, creator);
            args = convertFor(args, outputTypes);
            return creator.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[] convertFor(Object[] args, Parameter[] parameters)
    {
        for (int i = 0; i < args.length && i < parameters.length; i++)
        {
            Type prm = parameters[i].getParameterizedType();
            Object arg = args[i];
            Object value = convertRaw(arg, prm);
            args[i] = value;
        }
        return args;
    }

    private static Object convertRaw(Object v, Type r)
    {
        Class t = r instanceof Class<?> ? (Class)r : (Class) ((ParameterizedType)r).getRawType();
        if (t.isEnum())
            return v;
        if (t.equals(Object.class))
            return v;
        if (t.isInstance(v))
            return v;
        if (v instanceof Object[])
            return fromObjectArray(r, (Object[])v);
        return v;
    }

    public static Object toObjectArray(Object obj) {
        try {
            IDataType kind = DataTypes.getKind(obj);
            if (kind.Kind() != DataType.Unknown) {
                if (kind instanceof DataTypes.ArrayDt && ((DataTypes.ArrayDt)kind).Item().Kind() == DataType.Unknown)
                    obj = ArrayX.asObjectArray(obj);
                if (kind instanceof DataTypes.ListDt && ((DataTypes.ListDt)kind).Item().Kind() == DataType.Unknown)
                    obj = ArrayX.asObjectArray((Iterable)obj);
                if (obj instanceof Object[] && Arrays.stream((Object[])obj)
                        .anyMatch(a -> DataTypes.getKind(a).Kind() == DataType.Unknown)) {
                	Object[] ar = (Object[])obj;
                    for (int i = 0; i < ar.length; i++)
                        ar[i] = toObjectArray(ar[i]);
                }
                return obj;
            }
            Class type = obj.getClass();
            List<Property> props = Reflect.getProperties(type);
            Object[] args = new Object[props.size()];
            for (int i = 0; i < args.length; i++) {
                Property prop = props.get(i);
                Method propGet = prop.Get();
                Object propVal = propGet.invoke(obj);
                args[i] = toObjectArray(propVal);
            }
            return args;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
