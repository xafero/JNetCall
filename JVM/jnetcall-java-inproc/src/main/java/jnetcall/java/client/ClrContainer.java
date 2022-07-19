package jnetcall.java.client;

import com.sun.jna.Function;
import jnethotel.java.Clr;
import jnethotel.java.Natives;
import jnethotel.java.api.ICoreClr;
import org.javatuples.Pair;

public final class ClrContainer implements AutoCloseable {

    private final Clr _vm;
    private final Function _caller;

    public ClrContainer(String dll) {
        try {
            var tmp = setupClr(dll);
            _vm = tmp.getValue0();
            _caller = tmp.getValue1();
        } catch (Exception e) {
            throw new RuntimeException(dll, e);
        }
    }

    private static Pair<Clr, Function> setupClr(String dll) throws Exception {
        var vmRef = Natives.getVmRef();
        vmRef.loadLib();
        var clr = new Clr(vmRef);
        var caller = getCallCallback(clr.getCore(), dll);
        installStop(clr);
        return Pair.with(clr, caller);
    }

    private static void installStop(AutoCloseable vm) {
        var domain = Runtime.getRuntime();
        domain.addShutdownHook(new Thread(() -> {
            try {
                vm.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public byte[] sendAndGetArray(byte[] input) {
        var output = _vm.callStaticByteArrayMethod(_caller, input);
        return output;
    }

    private static Function getCallCallback(ICoreClr coreClr, String dll)
            throws Exception {
        final var bootType = "X.Boot";
        final var bootMethod = "Call";
        final var bootDelegate = "JNetCall.Sharp.API.CallDelegate, JNetCall.Sharp.InProc";
        return Clr.getCallback(coreClr, dll, bootType, bootMethod, bootDelegate);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
