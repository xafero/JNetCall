package jnethotel.java.linux.impl;

import com.sun.jna.Callback;
import jnethotel.java.interop.api.hostfxr_error_writer_fn;

public interface hostfxr_error_writer_fn_unix
    extends hostfxr_error_writer_fn<String>, Callback {
}
