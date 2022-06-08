package jnetcall.java.client;

import java.io.File;
import java.nio.file.Paths;

public final class ServiceEnv {

    private static String getBaseFolder() {
        try {
            var source = ServiceEnv.class.getProtectionDomain().getCodeSource();
            File jar = new File(source.getLocation().toURI().getPath());
            return jar.getParentFile().getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildPath(String path) {
        var baseDir = getBaseFolder();
        var destDir = Paths.get(baseDir, fixSlash(path))
                .normalize().toAbsolutePath().toString();
        return destDir;
    }

    private static String fixSlash(String path) {
        var sep = File.separator;
        return path.replace('/' + "", sep)
                .replace('\\' + "", sep);
    }

    public static File getCurrentDir() {
        var dir = Paths.get("").toAbsolutePath();
        var txt = dir.normalize().toString();
        return new File(txt);
    }
}
