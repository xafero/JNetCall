package jnethotel.java;

import com.sun.jna.Platform;
import jnethotel.java.api.IVmRef;
import jnethotel.java.linux.LinuxVmRef;
import jnethotel.java.mac.MacVmRef;
import jnethotel.java.windows.WinVmRef;

public final class Natives {

    public static IVmRef getVmRef() {
        if (isLinux())
            return new LinuxVmRef();
        if (isWindows())
            return new WinVmRef();
        if (isMac())
            return new MacVmRef();

        var desc = System.getProperty("os.name") + " " + System.getProperty("os.version");
        throw new UnsupportedOperationException(desc.trim());
    }

    private static boolean isWindows() {
        return Platform.isWindows();
    }

    private static boolean isLinux() {
        return Platform.isLinux();
    }

    private static boolean isMac() { return Platform.isMac(); }
}
