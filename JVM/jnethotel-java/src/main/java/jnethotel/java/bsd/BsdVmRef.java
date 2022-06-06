package jnethotel.java.bsd;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;
import jnethotel.java.linux.impl.nethost_library_unix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BsdVmRef implements IVmRef {

    private final String DllName = "libnethost.so";

    @Override
    public String getVmDll() {
        return DllName;
    }
    
    private NativeLibrary nativeLibrary;
    private nethost_library_unix instance;

    @Override
    public void loadLib() throws IOException {
        var libFileName = getVmDll();
        var libRoot = Path.of("/usr/share/dotnet");
        var libPath = Files.find(libRoot, 7,
                        (p, b) -> p.getFileName().toString().equals(libFileName))
                .findFirst().orElseThrow().getParent().toString();
        System.setProperty("jna.library.path", libPath);

        final String jnaLibraryName = "nethost";
        nativeLibrary = NativeLibrary.getInstance(jnaLibraryName);
        instance = Native.load(jnaLibraryName, nethost_library_unix.class);
    }

    @Override
    public ICoreClr getCoreClr() {
        return new BsdCoreClr(instance);
    }
}
