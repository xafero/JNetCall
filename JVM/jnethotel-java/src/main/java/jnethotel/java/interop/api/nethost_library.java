package jnethotel.java.interop.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface nethost_library<TBuffer> {

    int get_hostfxr_path(TBuffer buffer, PointerByReference buffer_size, Pointer parameters);
}
