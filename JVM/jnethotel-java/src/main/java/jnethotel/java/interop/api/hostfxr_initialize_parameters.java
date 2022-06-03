package jnethotel.java.interop.api;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public abstract class hostfxr_initialize_parameters extends Structure {
    public Pointer size;

    public hostfxr_initialize_parameters() {
        super();
    }

    public hostfxr_initialize_parameters(Pointer peer) {
        super(peer);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("size", "host_path", "dotnet_root");
    }
}
