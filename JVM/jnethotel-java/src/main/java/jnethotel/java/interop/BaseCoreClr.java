package jnethotel.java.interop;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.interop.api.hostfxr_library;
import jnethotel.java.interop.api.nethost_library;

import java.io.File;

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
        var load_assembly_and_get_function_pointer_fn = Pointer.NULL;

        var parameters = createByRef();

        var host_context_handle = Pointer.NULL;
        var ptr_host_context_handle = new PointerByReference(host_context_handle);

        var rc = hostfxr_library.hostfxr_initialize_for_runtime_config(wrap(runtime_config_path),
                parameters, ptr_host_context_handle);

        host_context_handle = ptr_host_context_handle.getValue();

        if (rc != 0 || host_context_handle == Pointer.NULL) {
            throw new UnsupportedOperationException("hostfxr_initialize_for_runtime_config failed with: " + rc);
        } else {
            var ptr_load_assembly_and_get_function_pointer_fn = new PointerByReference(load_assembly_and_get_function_pointer_fn);

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
        final var maxPath = 260;
        var buffer = allocate(maxPath);

        var buffer_size = Pointer.createConstant(maxPath);
        var ptr_buffer_size = new PointerByReference(buffer_size);

        if (nethost_library.get_hostfxr_path(buffer, ptr_buffer_size, Pointer.NULL) != 0) {
            return false;
        }

        var hostfxr_path = toString(buffer);

        System.out.printf("Resolved hostfxr to: %s %n", hostfxr_path);

        hostfxr_library = Native.load(hostfxr_path, getHostFxrClass());
        return true;
    }

    public Function load_assembly_and_get_callback(String runtime_config_path, String assembly_path,
                                                   String type_name, String method_name,
                                                   String delegate_type_name) throws Exception {
        if (!load_hostfxr()) {
            throw new Exception("Failed to resolve hostfxr");
        }

        var runtime_config_path_file = new File(runtime_config_path).getAbsoluteFile();
        System.out.printf("Resolved the runtime.config.json to: %s %n", runtime_config_path_file.toString());

        var load_assembly_and_get_function_pointer_fn = get_load_assembly_and_get_function_pointer_fn(runtime_config_path_file.toString());

        if (load_assembly_and_get_function_pointer_fn == Pointer.NULL) {
            throw new Exception("Failed to resolve load_assembly_and_get_function_pointer");
        }

        var managed_fn = Pointer.NULL;
        var ptr_managed_fn = new PointerByReference(managed_fn);

        var assembly_path_file = new File(assembly_path).getAbsoluteFile();

        System.out.printf("Resolved the .net library to: %s %n", assembly_path_file.toString());
        System.out.printf("Loading type: %s %n", type_name);
        System.out.printf("Locating method: %s %n", method_name);
        System.out.printf("Matching delegate: %s %n", delegate_type_name);

        var rc = (int) load_assembly_and_get_function_pointer_fn.invokeInt(new Object[]{
                wrap(assembly_path_file.toString()),
                wrap(type_name),
                wrap(method_name),
                wrap(delegate_type_name),
                Pointer.NULL,
                ptr_managed_fn
        });

        managed_fn = ptr_managed_fn.getValue();

        if (rc != 0 || managed_fn == Pointer.NULL) {
            var message = String.format("load_assembly_and_get_function_pointer failed with: %s", rc);
            throw new Exception(message);
        }

        return Function.getFunction(managed_fn, Function.C_CONVENTION);
    }
}
