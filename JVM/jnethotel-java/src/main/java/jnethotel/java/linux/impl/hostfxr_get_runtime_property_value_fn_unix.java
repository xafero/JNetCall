package jnethotel.java.linux.impl;

import com.sun.jna.Callback;
import jnethotel.java.interop.api.hostfxr_get_runtime_property_value_fn;

public interface hostfxr_get_runtime_property_value_fn_unix
  extends hostfxr_get_runtime_property_value_fn<String>, Callback {
}
