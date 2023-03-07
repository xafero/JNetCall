package jnethotel.java.mac;

import java.nio.ByteBuffer;

import com.sun.jna.Function;

import jnethotel.java.interop.BaseCoreClr;
import jnethotel.java.interop.api.hostfxr_delegate_type;
import jnethotel.java.linux.impl.hostfxr_initialize_parameters_unix;
import jnethotel.java.linux.impl.hostfxr_library_unix;
import jnethotel.java.linux.impl.nethost_library_unix;

class MacCoreClr extends BaseCoreClr<String, ByteBuffer, hostfxr_initialize_parameters_unix.ByReference> {

    MacCoreClr(nethost_library_unix instance) {
        nethost_library = instance;
        funcFlags = Function.C_CONVENTION;
        hdt_load_assembly_and_get_function_pointer = hostfxr_delegate_type.hdt_load_assembly_and_get_function_pointer;
    }

    @Override
    protected String wrap(String java) {
        return java;
    }

    @Override
    protected ByteBuffer allocate(int size) {
        return ByteBuffer.allocate(size);
    }

    @Override
    protected String toString(ByteBuffer buffer) {
        return new String(buffer.array());
    }

    @Override
    protected Class getHostFxrClass() {
        return hostfxr_library_unix.class;
    }

    @Override
    protected hostfxr_initialize_parameters_unix.ByReference createByRef() {
        return new hostfxr_initialize_parameters_unix.ByReference();
    }
}
