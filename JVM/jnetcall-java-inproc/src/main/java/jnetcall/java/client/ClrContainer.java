package jnetcall.java.client;

import org.javatuples.Pair;

import com.sun.jna.Function;

import jnethotel.java.Clr;
import jnethotel.java.Natives;
import jnethotel.java.api.ICoreClr;
import jnethotel.java.api.IVmRef;

public final class ClrContainer implements AutoCloseable {

    private final Clr _vm;
    private final Function _caller;

    public ClrContainer(String dll) {
        try {
            Pair<Clr, Function> tmp = setupClr(dll);
            _vm = tmp.getValue0();
            _caller = tmp.getValue1();
        } catch (Exception e) {
            throw new RuntimeException(dll, e);
        }
    }

    private static Pair<Clr, Function> setupClr(String dll) throws Exception {
        IVmRef vmRef = Natives.getVmRef();
        vmRef.loadLib();
        Clr clr = new Clr(vmRef);
        Function caller = getCallCallback(clr.getCore(), dll);
        installStop(clr);
        return Pair.with(clr, caller);
    }

    private static void installStop(AutoCloseable vm) {
        Runtime domain = Runtime.getRuntime();
        domain.addShutdownHook(new Thread(() -> {
            try {
                vm.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public byte[] sendAndGetArray(byte[] input) {
        byte[] output = _vm.callStaticByteArrayMethod(_caller, input);
        return output;
    }

    private static Function getCallCallback(ICoreClr coreClr, String dll)
            throws Exception {
        final String bootType = "X.Boot";
        final String bootMethod = "Call";
        final String bootDelegate = "JNetCall.Sharp.API.CallDelegate, JNetCall.Sharp.InProc";
        return Clr.getCallback(coreClr, dll, bootType, bootMethod, bootDelegate);
    }

    @Override
    public void close() {
        // NO-OP
    }
}
