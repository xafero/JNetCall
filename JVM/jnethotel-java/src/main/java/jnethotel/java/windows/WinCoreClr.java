package jnethotel.java.windows;

import java.nio.CharBuffer;

import com.sun.jna.Function;
import com.sun.jna.WString;

import jnethotel.java.interop.BaseCoreClr;
import jnethotel.java.interop.api.hostfxr_delegate_type;
import jnethotel.java.windows.impl.hostfxr_initialize_parameters_windows.ByReference;
import jnethotel.java.windows.impl.hostfxr_library_windows;
import jnethotel.java.windows.impl.nethost_library_windows;

class WinCoreClr extends BaseCoreClr<WString, CharBuffer, ByReference> {

    WinCoreClr(nethost_library_windows instance)
    {
        nethost_library = instance;
        funcFlags = Function.ALT_CONVENTION;
        hdt_load_assembly_and_get_function_pointer = hostfxr_delegate_type.hdt_load_assembly_and_get_function_pointer;
    }

    @Override
    protected WString wrap(String java) {
        return new WString(java);
    }

    @Override
    protected CharBuffer allocate(int size) {
        return CharBuffer.allocate(size);
    }

    @Override
    protected String toString(CharBuffer buffer) {
        return new String(buffer.array());
    }

    @Override
    protected Class getHostFxrClass() {
        return hostfxr_library_windows.class;
    }

    @Override
    protected ByReference createByRef() {
        return new ByReference();
    }
}
