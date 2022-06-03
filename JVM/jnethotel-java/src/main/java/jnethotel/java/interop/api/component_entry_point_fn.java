package jnethotel.java.interop.api;

import com.sun.jna.Pointer;

public interface component_entry_point_fn {

    int apply(Pointer arg, int arg_size_in_bytes);
}
