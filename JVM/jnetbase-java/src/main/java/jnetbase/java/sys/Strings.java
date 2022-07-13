package jnetbase.java.sys;

import java.io.*;

public final class Strings {

    public static int countMatches(CharSequence text, char letter) {
        if (text.length() == 0)
            return 0;
        var count = 0;
        for (var i = 0; i < text.length(); ++i)
            if (letter == text.charAt(i))
                ++count;
        return count;
    }

    public static String getStackTrace(Throwable error) {
        var bld = new StringWriter();
        var printer = new PrintWriter(bld, true);
        error.printStackTrace(printer);
        return bld.getBuffer().toString();
    }

    public static String repeat(int num, String c) {
        return new String(new char[num]).replace("\0", c);
    }

    public static String readToEnd(InputStream stream) throws IOException {
        return new String(stream.readAllBytes());
    }
}
