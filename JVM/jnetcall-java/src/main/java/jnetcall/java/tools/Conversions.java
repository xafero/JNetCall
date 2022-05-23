package jnetcall.java.tools;

public final class Conversions {

    public static Object[] convert(Class<?>[] types, Object[] args) {
        for (var i = 0; i < args.length; i++)
            args[i] = convert(types[i], args[i]);
        return args;
    }

    public static Object convert(Class<?> type, Object arg) {
        // TODO !
        return arg;
    }
}
