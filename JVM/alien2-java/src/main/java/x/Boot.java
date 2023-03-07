package x;

import org.example.impl.CalculatorService;

import jnetcall.java.server.ClassHosting;
import jnetcall.java.server.ServiceLots;

@SuppressWarnings("unused")
public final class Boot {

    static {
    	ClassHosting host = ServiceLots.create(CalculatorService.class);
        host.registerAll();
        host.serve();
    }

    public static byte[] call(byte[] input) throws Exception { return ServiceLots.call(input); }
}