package jnethotel.java.windows.impl;

import com.sun.jna.Callback;
import com.sun.jna.WString;
import jnethotel.java.interop.api.hostfxr_error_writer_fn;

public interface hostfxr_error_writer_fn_windows
        extends hostfxr_error_writer_fn<WString>, Callback {
}
