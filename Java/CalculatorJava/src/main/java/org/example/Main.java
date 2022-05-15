package org.example;

import com.jnetcall.java.ServiceHosts;
import org.example.api.ICalculator;
import org.example.impl.CalculatorService;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        var host = ServiceHosts.create(CalculatorService.class);
        host.addServiceEndpoint(ICalculator.class);

        host.open();
        System.out.println("The service is ready.");

        System.out.println("Press <Enter> to terminate the service.");
        System.out.println();
        System.in.read();
        host.close();

        System.out.println("Exited!");
    }
}