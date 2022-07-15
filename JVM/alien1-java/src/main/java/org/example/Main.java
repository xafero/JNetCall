package org.example;

import jnetcall.java.server.ServiceHosts;
import org.example.api.*;
import org.example.impl.CalculatorService;

public class Main {

    public static void main(String[] args) throws Exception {

        try (var host = ServiceHosts.create(CalculatorService.class)) {
            host.addServiceEndpoint(ICalculator.class);
            host.addServiceEndpoint(IDataTyped.class);
            host.addServiceEndpoint(IMultiple.class);
            host.addServiceEndpoint(IStringCache.class);
            host.addServiceEndpoint(ISimultaneous.class);

            host.open(System.in, System.out);
        }
    }
}