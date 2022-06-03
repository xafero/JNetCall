package jnethotel.java.interop;

public final class VmHelper {

    public static String getRuntimeConfig(String dll) {
        var base = dll.substring(0, dll.length() - 4);
        final String suffix = ".runtimeconfig.json";
        return base + suffix;
    }
}
