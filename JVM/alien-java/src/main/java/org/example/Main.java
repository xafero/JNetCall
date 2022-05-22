package org.example;

import org.example.api.ICalculator;
import org.example.api.IDataTyped;
import org.example.api.IMultiple;
import org.example.impl.CalculatorService;

import com.jnetcall.java.ServiceHosts;

public class Main {

    public static void main(String[] args) throws Exception {

        try (var host = ServiceHosts.create(CalculatorService.class)) {
            host.addServiceEndpoint(ICalculator.class);
            host.addServiceEndpoint(IDataTyped.class);
            host.addServiceEndpoint(IMultiple.class);

            host.open(System.in, System.out);
        }
    }
}