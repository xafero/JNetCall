package jnethotel.java.windows.impl;

import com.sun.jna.WString;

import jnethotel.java.interop.api.hostfxr_library;
import jnethotel.java.windows.impl.hostfxr_initialize_parameters_windows.ByReference;

public interface hostfxr_library_windows
    extends hostfxr_library<WString, ByReference> {
}
