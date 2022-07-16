package org.example;

import jnetcall.java.server.ServiceHosts;
import org.example.impl.CalculatorService;

public final class Main {

    public static void main(String[] args) throws Exception {

        try (var host = ServiceHosts.create(CalculatorService.class)) {
            host.registerAll();
            host.serveAndWait();
        }
    }
}