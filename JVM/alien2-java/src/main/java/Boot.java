import jnetcall.java.server.ServiceLots;
import org.example.api.*;
import org.example.impl.CalculatorService;

@SuppressWarnings("unused")
public final class Boot {

    public static void Init() throws Exception {
        var host = ServiceLots.create(CalculatorService.class);

        host.addServiceEndpoint(ICalculator.class);
        host.addServiceEndpoint(IDataTyped.class);
        host.addServiceEndpoint(IMultiple.class);
        host.addServiceEndpoint(IStringCache.class);
        host.addServiceEndpoint(ISimultaneous.class);

        host.build();
    }
}