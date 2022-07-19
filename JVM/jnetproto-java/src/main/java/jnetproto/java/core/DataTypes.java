package jnetproto.java.core;

import com.xafero.javaenums.Enums;
import jnetbase.java.meta.Reflect;
import jnetproto.java.api.DataType;
import org.javatuples.Tuple;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DataTypes {

    public static Class<?> toClass(Object a) {
        var kind = DataTypes.getKind(a);
        try {
            return DataTypes.getClass(kind.Kind());
        } catch (Exception e) {
            return a.getClass();
        }
    }

    public static byte getByte(IDataType kind) {
        return (byte) kind.Kind().ordinal();
    }

    public static DataType toDataType(int read) {
        return DataType.values()[read];
    }

    public static Class<?> getClass(DataType type) {
        switch (type) {
            case Bool: return boolean.class;
            case I8: return byte.class;
            case I16: return short.class;
            case I32: return int.class;
            case I64: return long.class;
            case F32: return float.class;
            case F64: return double.class;
            case F128: return BigDecimal.class;
            case Char: return char.class;
            case UTF8: return String.class;
            case Duration: return Duration.class;
            case Timestamp: return LocalDateTime.class;
            case Guid: return UUID.class;
            case Null: return Object.class;
            default: throw new IllegalArgumentException(type.toString());
        }
    }

    public interface IDataType { DataType Kind(); }
    private record SingleDt(DataType Kind) implements IDataType { }
    public record EnumDt(DataType Kind, Class<?> Type) implements IDataType { }
    public record ArrayDt(DataType Kind, int Rank, IDataType Item) implements IDataType {}
    public record MapDt(DataType Kind, IDataType Key, IDataType Val) implements IDataType {}
    public record ListDt(DataType Kind, IDataType Item) implements IDataType {}

    public static IDataType getKind(Object instance)
    {
        if (instance == null)
        {
            return new SingleDt(DataType.Null);
        }
        var type = instance instanceof Class<?> cl ? cl :
                instance instanceof ParameterizedType pt ? (Class<?>) pt.getRawType() :
                instance.getClass();
        if (Enums.isEnum(type))
        {
            var item = Enums.getEnumUnderlyingType(type);
            return new EnumDt(getKind(item).Kind(), item);
        }
        if (type.isArray())
        {
            var item = type.getComponentType();
            var rank = Reflect.getRank(type);
            if (rank == 1)
            {
                if (item == Object.class)
                    return new SingleDt(DataType.Bag);
                if (item == byte.class)
                    return new SingleDt(DataType.Binary);
            }
            return new ArrayDt(DataType.Array, rank, getKind(item));
        }
        if (Tuple.class.isAssignableFrom(type))
        {
            return new SingleDt(DataType.Tuple);
        }
        if (Map.class.isAssignableFrom(type))
        {
            var dict = (Map<?,?>)instance;
            Map.Entry<?,?> f = null;
            for (var entry : dict.entrySet())
            {
                f = (Map.Entry<?,?>)entry;
                break;
            }
            return new MapDt(DataType.Map, getKind(f.getKey()), getKind(f.getValue()));
        }
        if (Set.class.isAssignableFrom(type))
        {
            var item = instance instanceof Set<?> s
                    ? (s.isEmpty() ? Object.class : s.iterator().next())
                    : ((ParameterizedType)instance).getActualTypeArguments()[0];
            return new ListDt(DataType.Set, getKind(item));
        }
        if (List.class.isAssignableFrom(type))
        {
            var item = instance instanceof List<?> l
                    ? (l.isEmpty() ? Object.class : l.get(0))
                    : ((ParameterizedType)instance).getActualTypeArguments()[0];
            return new ListDt(DataType.List, getKind(item));
        }
        return new SingleDt(getSingleKind(type));
    }

    private static DataType getSingleKind(Class<?> type) {
        switch (type.getName()) {
            case "boolean": case "java.lang.Boolean": return DataType.Bool;
            case "byte": case "java.lang.Byte": return DataType.I8;
            case "short": case "java.lang.Short": return DataType.I16;
            case "int": case "java.lang.Integer": return DataType.I32;
            case "long": case "java.lang.Long": return DataType.I64;
            case "float": case "java.lang.Float": return DataType.F32;
            case "double": case "java.lang.Double": return DataType.F64;
            case "java.math.BigDecimal": return DataType.F128;
            case "char": case "java.lang.Character": return DataType.Char;
            case "java.lang.String": return DataType.UTF8;
            case "java.time.Duration": return DataType.Duration;
            case "java.time.LocalDateTime": return DataType.Timestamp;
            case "java.util.UUID": return DataType.Guid;
            default: return DataType.Unknown;
        }
    }
}
