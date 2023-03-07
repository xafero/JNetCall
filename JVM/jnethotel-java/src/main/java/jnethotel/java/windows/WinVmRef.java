package jnethotel.java.windows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import jnetbase.java.compat.J8;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;
import jnethotel.java.windows.impl.nethost_library_windows;

public final class WinVmRef implements IVmRef {

    private final String DllName = "nethost.dll";

    @Override
    public String getVmDll() {
        return DllName;
    }

    private NativeLibrary nativeLibrary;
    private nethost_library_windows instance;

    @Override
    public void loadLib() throws IOException {
        String libFileName = getVmDll();
        Path libRoot = Paths.get("C:\\Program Files\\dotnet");
        String libPath = J8.orElseThrow(Files.find(libRoot, 7,
                        (p, b) -> p.getFileName().toString().equals(libFileName) &&
                                p.getParent().toString().contains("-x64"))
                .findFirst()).getParent().toString();
        System.setProperty("jna.library.path", libPath);

        final String jnaLibraryName = "nethost";
        nativeLibrary = NativeLibrary.getInstance(jnaLibraryName);
        instance = Native.load(jnaLibraryName, nethost_library_windows.class);
    }

    @Override
    public ICoreClr getCoreClr() {
        return new WinCoreClr(instance);
    }
}
