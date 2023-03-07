package jnetcall.java.impl.util;

import java.lang.reflect.Method;

import org.javatuples.Pair;

import jnetbase.java.meta.Reflect;
import jnetcall.java.api.flow.MethodCall;

public final class ClassTools {

    private static final String AsyncAdd = "_A";

    public static Pair<String, String> toMethodId(MethodCall call) {
        String name = call.M().replace("_", "");
        int count = call.A().length;
        String id = (name + "_" + count).toLowerCase();
        String ida = (id + AsyncAdd).toLowerCase();
        return Pair.with(ida, id);
    }

    public static String toMethodId(Method method) {
        String name = method.getName().replace("_", "");
        int count = method.getParameters().length;
        String suffix = Reflect.isAsync(method) ? AsyncAdd : "";
        String id = (name + "_" + count + suffix).toLowerCase();
        return id;
    }

    public static boolean isSameMethod(Method m, String callName) {
        String mName = m.getName().replace("_", "");
        return mName.equalsIgnoreCase(callName);
    }

    public static String toDelegateId(Object del) {
        int hash = del.hashCode();
        Class type = del.getClass();
        Class interf = type.getInterfaces()[0];
        Method method = interf.getMethods()[0];
        return hash + "#" + method;
    }
}
