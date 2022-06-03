package jnethotel.java.interop.api;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface hostfxr_library<TString, TByRef> extends Library {

    int hostfxr_main(int argc, PointerByReference argv);

    int hostfxr_main_startupinfo(int argc, PointerByReference argv, TString host_path,
                                 TString dotnet_root, TString app_path);

    hostfxr_error_writer_fn hostfxr_set_error_writer(hostfxr_error_writer_fn error_writer);

    int hostfxr_initialize_for_dotnet_command_line(int argc, PointerByReference argv,
                                                   TByRef parameters,
                                                   PointerByReference host_context_handle);

    int hostfxr_initialize_for_runtime_config(TString runtime_config_path,
                                              TByRef parameters,
                                              PointerByReference host_context_handle);

    int hostfxr_set_runtime_property_value(Pointer host_context_handle, TString name, TString value);

    int hostfxr_get_runtime_properties(Pointer host_context_handle, PointerByReference count,
                                       PointerByReference keys, PointerByReference values);

    int hostfxr_run_app(Pointer host_context_handle);

    int hostfxr_get_runtime_delegate(Pointer host_context_handle, int type, PointerByReference delegate);

    int hostfxr_close(Pointer host_context_handle);
}
