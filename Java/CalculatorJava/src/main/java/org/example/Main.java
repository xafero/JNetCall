package org.example;

import com.jnetcall.java.ServiceHosts;
import org.example.api.ICalculator;
import org.example.impl.CalculatorService;

public class Main {

    public static void main(String[] args) throws Exception {

        try (var host = ServiceHosts.create(CalculatorService.class)) {
            host.addServiceEndpoint(ICalculator.class);

            host.open(System.in, System.out);
        }
    }
}