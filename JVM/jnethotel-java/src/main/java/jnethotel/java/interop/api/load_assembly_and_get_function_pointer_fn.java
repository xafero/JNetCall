package jnethotel.java.interop.api;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface load_assembly_and_get_function_pointer_fn<TString> {

    int apply(TString assembly_path, TString type_name, TString method_name, TString delegate_type_name,
              Pointer reserved, PointerByReference delegate);
}
