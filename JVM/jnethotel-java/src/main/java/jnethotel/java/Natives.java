package jnethotel.java;

import com.sun.jna.Platform;
import jnethotel.java.api.IVmRef;
import jnethotel.java.linux.LinuxVmRef;
import jnethotel.java.windows.WinVmRef;

public final class Natives {

    public static IVmRef getVmRef() {
        if (isLinux())
            return new LinuxVmRef();
        if (isWindows())
            return new WinVmRef();

        var desc = System.getProperty("os.name") + " " + System.getProperty("os.version");
        throw new UnsupportedOperationException(desc.trim());
    }

    private static boolean isWindows() {
        return Platform.isWindows();
    }

    private static boolean isLinux() {
        return Platform.isLinux();
    }
}
