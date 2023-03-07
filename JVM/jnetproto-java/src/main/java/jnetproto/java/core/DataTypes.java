package jnetproto.java.core;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.javatuples.Tuple;

import com.xafero.javaenums.Enums;

import jnetbase.java.meta.Reflect;
import jnetproto.java.api.DataType;

public final class DataTypes {

    public static Class<?> toClass(Object a) {
        IDataType kind = DataTypes.getKind(a);
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
    
    private static final class SingleDt implements IDataType {
    	private final DataType kind;

		public SingleDt(DataType kind) { 
    		this.kind=kind;
    	}
		
		@Override public DataType Kind() { return kind; }
    }
    
    public static final class EnumDt implements IDataType {     	
    	private final DataType kind;
		private final Class<?> type;

		public EnumDt(DataType kind, Class<?> type) { 
    		this.kind=kind;
    		this.type=type;
    	}
		
		@Override public DataType Kind() { return kind; }	
		public Class<?> Type() { return type; }
    }
    
    public static final class ArrayDt implements IDataType {
    	private final DataType kind;
    	private final int rank;
    	private final IDataType item;    	
    	    	
    	public ArrayDt(DataType kind, int rank, IDataType item) { 
    		this.kind=kind;
    		this.rank=rank;
    		this.item=item;
    	}
    	
    	@Override public DataType Kind() { return kind; }    	
    	public int Rank() { return rank; }    	
    	public IDataType Item() { return item; }
    }
    
    public static final class MapDt implements IDataType {
    	private final DataType kind;
    	private final IDataType key;
    	private final IDataType val;    	
    	
    	public MapDt(DataType kind, IDataType key, IDataType val) { 
    		this.kind=kind;
    		this.key=key;
    		this.val=val;
    	}
    	
    	@Override public DataType Kind() { return kind; }    	
    	public IDataType Key() { return key; }    	
    	public IDataType Val() { return val;	}
    }
    
    public static final class ListDt implements IDataType {
    	private final DataType kind;
    	private final IDataType item;
    	
    	public ListDt(DataType kind, IDataType item) {
    		this.kind=kind;
    		this.item=item;
    	}

    	@Override public DataType Kind() { return kind; }    	
		public IDataType Item() { return item; }
    }

    public static IDataType getKind(Object instance)
    {
        if (instance == null)
        {
            return new SingleDt(DataType.Null);
        }
        Class type = instance instanceof Class<?> ? (Class)instance :
                instance instanceof ParameterizedType ? (Class<?>)((ParameterizedType)instance).getRawType() :
                instance.getClass();
        if (Enums.isEnum(type))
        {
            Class item = Enums.getEnumUnderlyingType(type);
            return new EnumDt(getKind(item).Kind(), item);
        }
        if (type.isArray())
        {
            Class item = type.getComponentType();
            int rank = Reflect.getRank(type);
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
            Map<?, ?> dict = (Map<?,?>)instance;
            Map.Entry<?,?> f = null;
            for (Entry<?, ?> entry : dict.entrySet())
            {
                f = (Map.Entry<?,?>)entry;
                break;
            }
            return new MapDt(DataType.Map, getKind(f.getKey()), getKind(f.getValue()));
        }
        if (Set.class.isAssignableFrom(type))
        {
            Object item = instance instanceof Set<?>
                    ? (((Set)instance).isEmpty() ? Object.class : ((Set)instance).iterator().next())
                    : ((ParameterizedType)instance).getActualTypeArguments()[0];
            return new ListDt(DataType.Set, getKind(item));
        }
        if (List.class.isAssignableFrom(type))
        {
            Object item = instance instanceof List<?>
                    ? (((List)instance).isEmpty() ? Object.class : ((List)instance).get(0))
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
