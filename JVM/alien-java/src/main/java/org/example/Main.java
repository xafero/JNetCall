package org.example;

import jnetcall.java.server.ServiceHosts;
import org.example.api.ICalculator;
import org.example.api.IDataTyped;
import org.example.api.IMultiple;
import org.example.api.IStringCache;
import org.example.impl.CalculatorService;

public class Main {

    public static void main(String[] args) throws Exception {

        try (var host = ServiceHosts.create(CalculatorService.class)) {
            host.addServiceEndpoint(ICalculator.class);
            host.addServiceEndpoint(IDataTyped.class);
            host.addServiceEndpoint(IMultiple.class);
            host.addServiceEndpoint(IStringCache.class);

            host.open(System.in, System.out);
        }
    }
}