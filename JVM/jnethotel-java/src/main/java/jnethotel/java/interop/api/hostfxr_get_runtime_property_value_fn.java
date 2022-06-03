package jnethotel.java.interop.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface hostfxr_get_runtime_property_value_fn<TString> {

    int apply(Pointer host_context_handle, TString name, PointerByReference value);
}
