package jnethotel.java.interop;

import java.io.File;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import jnethotel.java.api.ICoreClr;
import jnethotel.java.interop.api.hostfxr_library;
import jnethotel.java.interop.api.nethost_library;

public abstract class BaseCoreClr<TString, TBuffer, TByRef> implements ICoreClr {

    protected abstract TString wrap(String java);
    protected abstract TBuffer allocate(int size);
    protected abstract String toString(TBuffer buffer);
    protected abstract Class<? extends hostfxr_library<TString, TByRef>> getHostFxrClass();
    protected abstract TByRef createByRef();

    protected hostfxr_library<TString, TByRef> hostfxr_library;
    protected nethost_library<TBuffer> nethost_library;
    protected int hdt_load_assembly_and_get_function_pointer;
    protected int funcFlags;

    public Function get_load_assembly_and_get_function_pointer_fn(String runtime_config_path) {
        Pointer load_assembly_and_get_function_pointer_fn = Pointer.NULL;

        TByRef parameters = createByRef();

        Pointer host_context_handle = Pointer.NULL;
        PointerByReference ptr_host_context_handle = new PointerByReference(host_context_handle);

        int rc = hostfxr_library.hostfxr_initialize_for_runtime_config(wrap(runtime_config_path),
                parameters, ptr_host_context_handle);

        host_context_handle = ptr_host_context_handle.getValue();

        if (rc != 0 || host_context_handle == Pointer.NULL) {
            throw new UnsupportedOperationException("hostfxr_initialize_for_runtime_config failed with: " + rc);
        } else {
            PointerByReference ptr_load_assembly_and_get_function_pointer_fn = new PointerByReference(load_assembly_and_get_function_pointer_fn);

            rc = hostfxr_library.hostfxr_get_runtime_delegate(host_context_handle,
                    hdt_load_assembly_and_get_function_pointer,
                    ptr_load_assembly_and_get_function_pointer_fn);

            load_assembly_and_get_function_pointer_fn = ptr_load_assembly_and_get_function_pointer_fn.getValue();

            if (rc != 0 || load_assembly_and_get_function_pointer_fn == Pointer.NULL) {
                throw new UnsupportedOperationException("hostfxr_get_runtime_delegate failed with: " + rc);
            }
        }

        hostfxr_library.hostfxr_close(host_context_handle);
        return Function.getFunction(load_assembly_and_get_function_pointer_fn, funcFlags);
    }

    public boolean load_hostfxr() {
        final int maxPath = 260;
        TBuffer buffer = allocate(maxPath);

        Pointer buffer_size = Pointer.createConstant(maxPath);
        PointerByReference ptr_buffer_size = new PointerByReference(buffer_size);

        if (nethost_library.get_hostfxr_path(buffer, ptr_buffer_size, Pointer.NULL) != 0) {
            return false;
        }

        String hostfxr_path = toString(buffer);
        hostfxr_library = Native.load(hostfxr_path, getHostFxrClass());
        return true;
    }

    public Function load_assembly_and_get_callback(String runtime_config_path, String assembly_path,
                                                   String type_name, String method_name,
                                                   String delegate_type_name) throws Exception {
        if (!load_hostfxr()) {
            throw new Exception("Failed to resolve hostfxr");
        }

        File runtime_config_path_file = new File(runtime_config_path).getAbsoluteFile();
        Function load_assembly_and_get_function_pointer_fn = get_load_assembly_and_get_function_pointer_fn(runtime_config_path_file.toString());

        if (load_assembly_and_get_function_pointer_fn == Pointer.NULL) {
            throw new Exception("Failed to resolve load_assembly_and_get_function_pointer");
        }

        Pointer managed_fn = Pointer.NULL;
        PointerByReference ptr_managed_fn = new PointerByReference(managed_fn);

        File assembly_path_file = new File(assembly_path).getAbsoluteFile();

        int rc = load_assembly_and_get_function_pointer_fn.invokeInt(new Object[]{
                wrap(assembly_path_file.toString()),
                wrap(type_name),
                wrap(method_name),
                wrap(delegate_type_name),
                Pointer.NULL,
                ptr_managed_fn
        });

        managed_fn = ptr_managed_fn.getValue();

        if (rc != 0 || managed_fn == Pointer.NULL) {
            String message = String.format("load_assembly_and_get_function_pointer failed with: %s", rc);
            throw new Exception(message);
        }

        return Function.getFunction(managed_fn, Function.C_CONVENTION);
    }
}
