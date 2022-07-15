package jnetcall.java.impl.util;

import jnetbase.java.meta.Reflect;
import jnetcall.java.api.flow.MethodCall;
import org.javatuples.Pair;

import java.lang.reflect.Method;

public final class ClassTools {

    private static final String AsyncAdd = "_A";

    public static Pair<String, String> toMethodId(MethodCall call) {
        var name = call.M().replace("_", "");
        var count = call.A().length;
        var id = (name + "_" + count).toLowerCase();
        var ida = (id + AsyncAdd).toLowerCase();
        return Pair.with(ida, id);
    }

    public static String toMethodId(Method method) {
        var name = method.getName().replace("_", "");
        var count = method.getParameters().length;
        var suffix = Reflect.isAsync(method) ? AsyncAdd : "";
        var id = (name + "_" + count + suffix).toLowerCase();
        return id;
    }

    public static boolean isSameMethod(Method m, String callName) {
        var mName = m.getName().replace("_", "");
        return mName.equalsIgnoreCase(callName);
    }

    public static String toDelegateId(Object del) {
        var hash = del.hashCode();
        var type = del.getClass();
        var interf = type.getInterfaces()[0];
        var method = interf.getMethods()[0];
        return hash + "#" + method;
    }
}
