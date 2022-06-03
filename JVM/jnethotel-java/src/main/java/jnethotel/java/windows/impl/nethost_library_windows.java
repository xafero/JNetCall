package jnethotel.java.windows.impl;

import com.sun.jna.win32.StdCallLibrary;
import jnethotel.java.interop.api.nethost_library;

import java.nio.CharBuffer;

public interface nethost_library_windows
    extends nethost_library<CharBuffer>, StdCallLibrary {
}
