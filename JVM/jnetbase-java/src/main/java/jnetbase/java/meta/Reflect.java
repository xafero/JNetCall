package jnetbase.java.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiFunction;

import jnetbase.java.sys.*;

public final class Reflect {

    public static boolean isAsync(Method method) {
        return isAsync(method.getGenericReturnType());
    }

    public static boolean isAsync(Type ret) {
        return ret instanceof ParameterizedType pt
                && pt.getRawType() instanceof Class<?> rc
                && Future.class.isAssignableFrom(rc);
    }

    public static boolean isDelegate(Object del) {
        if (del == null)
            return false;
        if (del.getClass().getName().contains("Lambda"))
            return true;
        if (del instanceof Class<?> c && c.isInterface() && c.getMethods().length == 1)
            return true;
        return false;
    }

    public static Type getTaskType(Type taskType, Type defaultArg) {
        var taskArg = taskType instanceof ParameterizedType pt
                ? Arrays.stream(pt.getActualTypeArguments()).findFirst().orElse(null)
                : null;
        if (taskArg == null) {
            return defaultArg == null ? Object.class : defaultArg;
        }
        return taskArg;
    }

    public static Method getMethod(BiFunction<Object, Object[], Object> func) {
        var type = func.getClass();
        var fields = type.getDeclaredFields();
        var field = fields[0];
        var raw = getField(field, func);
        var method = (Method) raw;
        return method;
    }

    public static Object getField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getTheMethod(Object delegate) {
        var type = delegate.getClass();
        var interfaces = type.getInterfaces();
        var interf = interfaces[0];
        var methods = interf.getMethods();
        var method = methods[0];
        return method;
    }

    public static InvocationHandler getProxyHandler(Object obj) {
        try {
            return Proxy.getInvocationHandler(obj);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int getRank(Object value) {
        return getRank(value.getClass());
    }

    public static int getRank(Class<?> type) {
        if (!type.isArray())
            return 0;
        return Strings.countMatches(type.getName(), '[');
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

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class[] types) {
        try {
            return clazz.getConstructor(types);
        } catch (NoSuchMethodException e) {
            return (Constructor<T>) clazz.getConstructors()[0];
        }
    }

    public static <T> T getVal(Future<T> task) {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createNew(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
