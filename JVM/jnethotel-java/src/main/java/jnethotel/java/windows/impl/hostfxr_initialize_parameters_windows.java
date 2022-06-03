package jnethotel.java.windows.impl;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import jnethotel.java.interop.api.hostfxr_initialize_parameters;

public class hostfxr_initialize_parameters_windows
    extends hostfxr_initialize_parameters {

    public WString host_path;
    public WString dotnet_root;

    public hostfxr_initialize_parameters_windows() {
        super();
    }

    public hostfxr_initialize_parameters_windows(Pointer size, WString host_path, WString dotnet_root) {
        super();

        this.size = size;
        this.host_path = host_path;
        this.dotnet_root = dotnet_root;
    }

    public static class ByReference extends hostfxr_initialize_parameters_windows implements Structure.ByReference {
    }

    public static class ByValue extends hostfxr_initialize_parameters_windows implements Structure.ByValue {
    }
}
