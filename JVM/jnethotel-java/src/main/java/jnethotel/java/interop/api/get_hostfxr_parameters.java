package jnethotel.java.interop.api;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public abstract class get_hostfxr_parameters<TString> extends Structure {
    public Pointer size;
    public TString assembly_path;
    public TString dotnet_root;

    public get_hostfxr_parameters() {
        super();
    }

    public get_hostfxr_parameters(Pointer peer) {
        super(peer);
    }

    public get_hostfxr_parameters(Pointer size, TString assembly_path, TString dotnet_root) {
        super();

        this.size = size;
        this.assembly_path = assembly_path;
        this.dotnet_root = dotnet_root;
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("size", "assembly_path", "dotnet_root");
    }

    public static class ByReference extends get_hostfxr_parameters implements Structure.ByReference {
    }

    public static class ByValue extends get_hostfxr_parameters implements Structure.ByValue {
    }
}
