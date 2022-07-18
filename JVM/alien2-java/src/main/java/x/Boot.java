package x;

import jnetcall.java.server.ServiceLots;
import org.example.api.*;
import org.example.impl.CalculatorService;

@SuppressWarnings("unused")
public final class Boot {

    static {
        var host = ServiceLots.create(CalculatorService.class);

        // TODO ?!
        // host.registerAll();
        // host.serveAndWait();

        host.addServiceEndpoint(ICalculator.class);
        host.addServiceEndpoint(IDataTyped.class);
        host.addServiceEndpoint(IMultiple.class);
        host.addServiceEndpoint(IStringCache.class);
        host.addServiceEndpoint(ISimultaneous.class);

        host.build();
    }

    public static byte[] call(byte[] input) throws Exception { return ServiceLots.call(input); }
}