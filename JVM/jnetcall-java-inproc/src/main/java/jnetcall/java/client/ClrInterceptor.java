package jnetcall.java.client;

import com.sun.jna.Function;
import jnetbase.java.io.ByteArrayStream;
import jnetcall.java.api.MethodResult;
import jnethotel.java.Clr;
import jnethotel.java.Natives;
import jnethotel.java.api.ICoreClr;
import jnetproto.java.beans.ProtoConvert;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public final class ClrInterceptor extends AbstractInterceptor {

    public ClrInterceptor(String exe) {
        super(exe);
    }

    private static final Object Sync = new Object();
    private static Clr _vm;
    private static Function _caller;

    private static Clr setupClr(String exe) throws Exception {
        synchronized (Sync) {
            if (_vm != null)
                return _vm;

            var vmRef = Natives.getVmRef();
            vmRef.loadLib();
            var clr = new Clr(vmRef);
            _caller = getCallCallback(clr.getCore(), exe);
            return clr;
        }
    }

    @Override
    protected void prepare() {
        synchronized (Sync) {
            try {
                _vm = setupClr(_exe);
            } catch (Exception e) {
                throw new RuntimeException(_exe, e);
            }
        }
    }

    @Override
    protected void start() {
    }

    private static Function getCallCallback(ICoreClr coreClr, String dll)
            throws Exception {
        final var bootType = "X.Boot";
        final var bootMethod = "Call";
        final var bootDelegate = "JNetCall.Sharp.API.CallDelegate, JNetCall.Sharp.InProc";
        return Clr.getCallback(coreClr, dll, bootType, bootMethod, bootDelegate);
    }

    private byte[] sendAndGetArray(byte[] input)
    {
        var output = _vm.callStaticByteArrayMethod(_caller, input);
        return output;
    }

    @Override
    protected void stop(int milliseconds) {
        var domain = Runtime.getRuntime();
        domain.addShutdownHook(new Thread(() -> {
            try {
                _vm.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (var input = new ByteArrayStream();
             var output = new ByteArrayOutputStream();
             var proto = new ProtoConvert(input, output, settings)) {
            var call = pack(method, args);
            if (call == null)
                return null;
            write(proto, call);
            var array = output.toByteArray();
            var result = sendAndGetArray(array);
            input.write(result);
            input.reset();
            var answer = read(MethodResult.class, proto);
            return unpack(method, answer);
        }
    }

    @Override
    protected String getErrorDetails() {
        return "";
    }
}
