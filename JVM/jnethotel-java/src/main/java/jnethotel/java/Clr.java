package jnethotel.java;

import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;
import jnethotel.java.interop.VmHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Clr implements AutoCloseable {

    private final IVmRef _vmRef;
    private final ICoreClr _core;

    public Clr(IVmRef vmRef) {
        _vmRef = vmRef;
        _core = _vmRef.getCoreClr();
    }

    @Override
    public void close() {
    }

    public static Function getCallback(ICoreClr coreClr, String dll, String type,
                                       String method, String delegate)
            throws Exception {
        var runtimeConfig = VmHelper.getRuntimeConfig(dll);
        var typeName = type.contains(", ") ? type : VmHelper.getTypeName(type, dll);

        if (!(new File(dll).exists()))
            throw new FileNotFoundException(dll);
        if (!(new File(runtimeConfig).exists()))
            throw new FileNotFoundException(runtimeConfig);

        return coreClr.load_assembly_and_get_callback(runtimeConfig, dll, typeName, method, delegate);
    }

    private static Memory toPointer(byte[] data) {
        var pointer = new Memory(data.length);
        pointer.write(0, data, 0, data.length);
        return pointer;
    }

    private static byte[] toByteArray(Pointer ptr, int size) {
        if (size == -1) {
            final int header = 4;
            var lenBits = ptr.getByteArray(0, header);
            var len = ByteBuffer.wrap(lenBits).order(ByteOrder.nativeOrder()).getInt();
            size = header + len;
        }
        var array = new byte[size];
        for (var i = 0; i < array.length; i++)
            array[i] = ptr.getByte(i);
        return array;
    }

    public ICoreClr getCore() {
        return _core;
    }

    public byte[] callStaticByteArrayMethod(Function func, byte[] input) {
        var inputPtr = toPointer(input);
        var outputPtr = func.invokePointer(new Object[]{inputPtr});
        return toByteArray(outputPtr, -1);
    }
}
