package jnetproto.java;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public final class DataTypes {
    public static Class getClass(DataType type) {
        switch (type) {
            case I8: return byte.class;
            case I16: return short.class;
            case I32: return int.class;
            case I64: return long.class;
            case F32: return float.class;
            case F64: return double.class;
            case F128: return BigDecimal.class;
            case UTF8: return String.class;
            case Duration: return Duration.class;
            case Timestamp: return LocalDateTime.class;
            case Guid: return UUID.class;
            default: throw new IllegalArgumentException(type.toString());
        }
    }

    public static DataType getKind(Class type) {
        switch (type.getName()) {
            case "java.lang.Byte": return DataType.I8;
            case "java.lang.Short": return DataType.I16;
            case "java.lang.Integer": return DataType.I32;
            case "java.lang.Long": return DataType.I64;
            case "java.lang.Float": return DataType.F32;
            case "java.lang.Double": return DataType.F64;
            case "java.math.BigDecimal": return DataType.F128;
            case "java.lang.String": return DataType.UTF8;
            case "java.time.Duration": return DataType.Duration;
            case "java.time.LocalDateTime": return DataType.Timestamp;
            case "java.util.UUID": return DataType.Guid;
            default: throw new IllegalArgumentException(type.toString());
        }
    }
}