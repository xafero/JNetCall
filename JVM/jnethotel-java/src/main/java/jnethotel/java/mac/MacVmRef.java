package jnethotel.java.mac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;
import jnethotel.java.linux.impl.nethost_library_unix;

public final class MacVmRef implements IVmRef {

    private final String DllName = "libnethost.dylib";

    @Override
    public String getVmDll() {
        return DllName;
    }

    private NativeLibrary nativeLibrary;
    private nethost_library_unix instance;

    @Override
    public void loadLib() throws IOException {
        String libFileName = getVmDll();
        Path libRoot = Path.of("/usr/local/share/dotnet/");
        String libPath = Files.find(libRoot, 7,
                        (p, b) -> p.getFileName().toString().equals(libFileName))
                .findFirst().orElseThrow().getParent().toString();
        System.setProperty("jna.library.path", libPath);

        final String jnaLibraryName = "nethost";
        nativeLibrary = NativeLibrary.getInstance(jnaLibraryName);
        instance = Native.load(jnaLibraryName, nethost_library_unix.class);
    }

    @Override
    public ICoreClr getCoreClr() {
        return new MacCoreClr(instance);
    }
}
