package jnethotel.java.linux.impl;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import jnethotel.java.interop.api.hostfxr_initialize_parameters;

public class hostfxr_initialize_parameters_unix
    extends hostfxr_initialize_parameters {

    public String host_path;
    public String dotnet_root;

    public hostfxr_initialize_parameters_unix() {
        super();
    }

    public hostfxr_initialize_parameters_unix(Pointer size, String host_path, String dotnet_root) {
        super();

        this.size = size;
        this.host_path = host_path;
        this.dotnet_root = dotnet_root;
    }

    public static class ByReference extends hostfxr_initialize_parameters_unix implements Structure.ByReference {
    }

    public static class ByValue extends hostfxr_initialize_parameters_unix implements Structure.ByValue {
    }
}
