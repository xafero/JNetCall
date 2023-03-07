package jnethotel.java.interop;

import java.io.File;

public final class VmHelper {

    public static String getRuntimeConfig(String dll) {
        String base = removeDll(dll);
        final String suffix = ".runtimeconfig.json";
        return base + suffix;
    }

    private static String removeDll(String dll) {
        return dll.substring(0, dll.length() - 4);
    }

    public static String getTypeName(String typeName, String dll) {
        String assemblyShort = (new File(dll)).getName();
        String assemblyName = removeDll(assemblyShort);
        return typeName + ", " + assemblyName;
    }

    public static String getNext(String dll, String name) {
        String assemblyDir = (new File(dll)).getParent();
        return (new File(assemblyDir, name + ".dll")).toString();
    }
}
