package jnethotel.java.windows.impl;

import com.sun.jna.Callback;
import com.sun.jna.WString;

import jnethotel.java.interop.api.hostfxr_get_runtime_property_value_fn;

public interface hostfxr_get_runtime_property_value_fn_windows
    extends hostfxr_get_runtime_property_value_fn<WString>, Callback {
}
