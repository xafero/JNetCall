package jnetbase.java.meta;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {

    private final Type _base;
    private final Type _arg;

    public TypeToken() {
        _base = getClass().getGenericSuperclass();
        _arg = ((ParameterizedType) _base).getActualTypeArguments()[0];
    }

    private TypeToken(Type type) {
        _base = getClass();
        _arg = type;
    }

    public Class<T> toClass() {
        return (Class<T>) _arg;
    }

    public Type toType() {
        return _arg;
    }

    @Override
    public String toString() {
        return _arg.toString();
    }

    public static <T> TypeToken<T> wrap(Type type) {
        return new TypeToken<T>(type);
    }

    public static <T> TypeToken<T> wrap(Class<T> clazz) {
        return new TypeToken<T>(clazz);
    }
}
