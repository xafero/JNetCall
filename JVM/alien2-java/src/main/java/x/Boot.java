package x;

import jnetcall.java.server.ServiceLots;
import org.example.impl.CalculatorService;

@SuppressWarnings("unused")
public final class Boot {

    static {
        var host = ServiceLots.create(CalculatorService.class);
        host.registerAll();
        host.serve();
    }

    public static byte[] call(byte[] input) throws Exception { return ServiceLots.call(input); }
}