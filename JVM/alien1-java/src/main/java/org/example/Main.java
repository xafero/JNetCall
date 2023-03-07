package org.example;

import org.example.impl.CalculatorService;

import jnetcall.java.server.ClassHosting;
import jnetcall.java.server.ServiceHosts;

public final class Main {

    public static void main(String[] args) throws Exception {

        try (ClassHosting host = ServiceHosts.create(CalculatorService.class)) {
            host.registerAll();
            host.serveAndWait();
        }
    }
}