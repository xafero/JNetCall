package jnetcall.java.client.tools;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

public final class ServiceEnv {

    private static String getBaseFolder() {
        try {
            CodeSource source = ServiceEnv.class.getProtectionDomain().getCodeSource();
            File jar = new File(source.getLocation().toURI().getPath());
            return jar.getParentFile().getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildPath(String path) {
        String baseDir = getBaseFolder();
        String destDir = Paths.get(baseDir, fixSlash(path))
                .normalize().toAbsolutePath().toString();
        return destDir;
    }

    private static String fixSlash(String path) {
        String sep = File.separator;
        return path.replace('/' + "", sep)
                .replace('\\' + "", sep);
    }

    public static File getCurrentDir() {
        Path dir = Paths.get("").toAbsolutePath();
        String txt = dir.normalize().toString();
        return new File(txt);
    }
}
