package jnethotel.java.api;

import com.sun.jna.Function;

public interface ICoreClr {

    Function load_assembly_and_get_callback(String runtime_config_path, String assembly_path,
                                            String type_name, String method_name,
                                            String delegate_type_name) throws Exception;

    Function get_load_assembly_and_get_function_pointer_fn(String runtime_config_path);

    boolean load_hostfxr();
}
