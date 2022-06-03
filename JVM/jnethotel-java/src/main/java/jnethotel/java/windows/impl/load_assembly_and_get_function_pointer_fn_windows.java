package jnethotel.java.windows.impl;

import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import jnethotel.java.interop.api.load_assembly_and_get_function_pointer_fn;

public interface load_assembly_and_get_function_pointer_fn_windows
        extends load_assembly_and_get_function_pointer_fn<WString>, StdCallCallback {
}
