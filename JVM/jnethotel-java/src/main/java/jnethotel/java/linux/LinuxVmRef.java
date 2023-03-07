package jnethotel.java.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import jnetbase.java.compat.J8;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;
import jnethotel.java.linux.impl.nethost_library_unix;

public final class LinuxVmRef implements IVmRef {

    private final String DllName = "libnethost.so";

    @Override
    public String getVmDll() {
        return DllName;
    }

    private NativeLibrary nativeLibrary;
    private nethost_library_unix instance;

    @Override
    public void loadLib() throws IOException {
        String libFileName = getVmDll();
        Path libRoot = Paths.get("/usr/share/dotnet");
        String libPath = J8.orElseThrow(Files.find(libRoot, 7,
                        (p, b) -> p.getFileName().toString().equals(libFileName))
                .findFirst()).getParent().toString();
        System.setProperty("jna.library.path", libPath);

        final String jnaLibraryName = "nethost";
        nativeLibrary = NativeLibrary.getInstance(jnaLibraryName);
        instance = Native.load(jnaLibraryName, nethost_library_unix.class);
    }

    @Override
    public ICoreClr getCoreClr() {
        return new LinuxCoreClr(instance);
    }
}
