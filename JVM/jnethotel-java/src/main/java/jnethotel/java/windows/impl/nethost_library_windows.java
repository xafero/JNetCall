package jnethotel.java.windows.impl;

import java.nio.CharBuffer;

import com.sun.jna.win32.StdCallLibrary;

import jnethotel.java.interop.api.nethost_library;

public interface nethost_library_windows
    extends nethost_library<CharBuffer>, StdCallLibrary {
}
