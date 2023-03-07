package jnetbase.java.sys;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import jnetbase.java.compat.J8;

public final class Strings {

    public static int countMatches(CharSequence text, char letter) {
        if (text.length() == 0)
            return 0;
        int count = 0;
        for (int i = 0; i < text.length(); ++i)
            if (letter == text.charAt(i))
                ++count;
        return count;
    }

    public static String getStackTrace(Throwable error) {
        StringWriter bld = new StringWriter();
        PrintWriter printer = new PrintWriter(bld, true);
        error.printStackTrace(printer);
        return bld.getBuffer().toString();
    }

    public static String repeat(int num, String c) {
        return new String(new char[num]).replace("\0", c);
    }

    public static String readToEnd(InputStream stream) throws IOException {
        return new String(J8.readAllBytes(stream));
    }
}
