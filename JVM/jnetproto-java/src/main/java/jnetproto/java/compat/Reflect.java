package jnetproto.java.compat;

import jnetproto.java.DataType;
import jnetproto.java.DataTypes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Reflect {

    public static int getRank(Object value) {
        return getRank(value.getClass());
    }

    public static int getRank(Class type) {
        if (!type.isArray())
            return 0;
        return Strings.countMatches(type.getName(), '[');
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
            throw new RuntimeException(method.toString(), e);
        }
    }

    public static Method getMethod(Object obj, String name, Class<?>... args) {
        var clazz = obj.getClass();
        try {
            return clazz.getMethod(name, args);
        } catch (Exception e) {
            throw new RuntimeException(clazz.toString(), e);
        }
    }

    public static List<Property> getProperties(Class<?> type) {
        var getMap = new HashMap<String, Method>();
        var setMap = new HashMap<String, Method>();
        for (var method : type.getMethods()) {
            var name = method.getName();
            if (name.equalsIgnoreCase("getClass"))
                continue;
            if (Character.isUpperCase(name.charAt(0))) {
                getMap.put(name, method);
                continue;
            }
            if (name.startsWith("get")) {
                getMap.put(name.substring(3), method);
                continue;
            }
            if (name.startsWith("is")) {
                getMap.put(name.substring(2), method);
                continue;
            }
            if (name.startsWith("set")) {
                setMap.put(name.substring(3), method);
                continue;
            }
        }
        var props = new ArrayList<Property>();
        var creator = type.getConstructors()[0];
        for (var params : creator.getParameters()) {
            var parmName = params.getName();
            var getter = getMap.get(parmName);
            var setter = setMap.get(parmName);
            props.add(new Property(parmName, getter, setter));
        }
        return props;
    }

    public record Property(String Name, Method Get, Method Set) { };

    public static Class toClass(Object a) {
        var kind = DataTypes.getKind(a);
        var clazz = DataTypes.getClass(kind.Kind());
        return clazz;
    }
}
