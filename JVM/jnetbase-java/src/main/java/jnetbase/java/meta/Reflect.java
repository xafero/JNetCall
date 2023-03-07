package jnetbase.java.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import jnetbase.java.sys.Strings;

public final class Reflect {

    public static boolean isAsync(Method method) {
        return isAsync(method.getGenericReturnType());
    }

    public static boolean isAsync(Type ret) {
    	if (!(ret instanceof ParameterizedType))
    		return false;
    	ParameterizedType pt = (ParameterizedType)ret;
    	Type raw = pt.getRawType();
        return raw instanceof Class<?>
            && Future.class.isAssignableFrom((Class<?>)raw);
    }

    public static boolean isDelegate(Object del) {
        if (del == null)
            return false;
        if (del.getClass().getName().contains("Lambda"))
            return true;
        if (del instanceof Class<?>) {
        	Class<?> c = (Class<?>)del;
        	if (c.isInterface() && c.getMethods().length == 1)
        		return true;
        }
        return false;
    }

    public static Type getTaskType(Type taskType, Type defaultArg) {
        Type taskArg = taskType instanceof ParameterizedType
                ? Arrays.stream(((ParameterizedType)taskType).getActualTypeArguments()).findFirst().orElse(null)
                : null;
        if (taskArg == null) {
            return defaultArg == null ? Object.class : defaultArg;
        }
        return taskArg;
    }

    public static Method getMethod(BiFunction<Object, Object[], Object> func) {
        Class<? extends BiFunction> type = func.getClass();
        Field[] fields = type.getDeclaredFields();
        Field field = fields[0];
        Object raw = getField(field, func);
        Method method = (Method) raw;
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
        Class<? extends Object> type = delegate.getClass();
        Class<?>[] interfaces = type.getInterfaces();
        Class<?> interf = interfaces[0];
        Method[] methods = interf.getMethods();
        Method method = methods[0];
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
        Class<? extends Object> clazz = obj.getClass();
        try {
            return clazz.getMethod(name, args);
        } catch (Exception e) {
            throw new RuntimeException(clazz.toString(), e);
        }
    }

    public static List<Property> getProperties(Type type)
    {
        return getProperties(extractRawClass(type));
    }

    public static List<Property> getProperties(Class<?> type) {
        HashMap<String, Method> getMap = new HashMap<String, Method>();
        HashMap<String, Method> setMap = new HashMap<String, Method>();
        for (Method method : type.getMethods()) {
            String name = method.getName();
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
        ArrayList<Property> props = new ArrayList<Property>();
        Constructor<?> creator = type.getConstructors()[0];
        for (Parameter params : creator.getParameters()) {
            String parmName = params.getName();
            Method getter = getMap.get(parmName);
            Method setter = setMap.get(parmName);
            if (getter == null && setter == null) {
            	ParamName pna = params.getAnnotation(ParamName.class);
            	parmName = pna != null ? pna.value() : null;
            	if (parmName != null) {
            		getter = getMap.get(parmName);
            		setter = setMap.get(parmName);            		
            	}
            }
            props.add(new Property(parmName, getter, setter));
        }
        return props;
    }

    public static Class extractRawClass(Type type)
    {
        if (type instanceof ParameterizedType) {
        	ParameterizedType pt = (ParameterizedType)type;
        	Type raw = pt.getRawType();
            if (raw instanceof Class<?>) {
                return (Class<?>)raw;
            }
        }
        return (Class) type;
    }

    public static <T> Constructor<T> getConstructor(Type type, Class[] types) {
        return getConstructor(extractRawClass(type), types);
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

    public static <T> T createNew(Type type) {
        return (T) createNew(extractRawClass(type));
    }

    public static <T> T createNew(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getFirstConstructor(Type type)
    {
        return getFirstConstructor(extractRawClass(type));
    }

    public static <T> Constructor<T> getFirstConstructor(Class<T> clazz) {
        for (Constructor<?> item : clazz.getConstructors()  )
            return (Constructor<T>) item;
        return null;
    }

    public static List<Class<?>> getInterfaces(Class<?> type) {
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        getAllInterfaces(type, interfaces);
        return new ArrayList<>(interfaces);
    }

    private static void getAllInterfaces(Class<?> type, Set<Class<?>> found) {
        while (type != null)
        {
            for (Class<?> interf : type.getInterfaces())
                if (found.add(interf))
                    getAllInterfaces(interf, found);
            type = type.getSuperclass();
        }
    }
}
