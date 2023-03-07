package org.example;

import static jnetcall.java.client.tools.ServiceEnv.buildPath;

import org.example.api.ICalculator;

import jnetcall.java.client.InProcClient;

public class Main {

    public static void main(String[] args) throws Exception {

        final String path = "..\\..\\..\\NET\\Alien2.Sharp\\bin\\Debug\\net6.0\\Alien2.Sharp.dll";

        ICalculator client = InProcClient.create(ICalculator.class, buildPath(path));
        System.out.println(" *** " + client.getName() + " on JVM *** ");
        
        double value1 = 100.00D;
        double value2 = 15.99D;
        double result = client.add(value1, value2);
        System.out.printf("Add(%s %s) = %s %n", value1, value2, result);

        value1 = 145.00D;
        value2 = 76.54D;
        result = client.subtract(value1, value2);
        System.out.printf("Subtract(%s %s) = %s %n", value1, value2, result);

        value1 = 9.00D;
        value2 = 81.25D;
        result = client.multiply(value1, value2);
        System.out.printf("Multiply(%s %s) = %s %n", value1, value2, result);

        value1 = 22.00D;
        value2 = 7.00D;
        result = client.divide(value1, value2);
        System.out.printf("Divide(%s %s) = %s %n", value1, value2, result);

        System.out.println("\nPress <Enter> to terminate the client.");
        System.in.read();
        client.close();
    }
}