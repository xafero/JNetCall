package org.example;

import jnetcall.java.client.ServiceClient;
import org.example.api.ICalculator;

import static jnetcall.java.client.ServiceEnv.buildPath;

public class Main {

    public static void main(String[] args) throws Exception {

        final String path = "..\\..\\..\\NET\\Alien.Sharp\\bin\\Debug\\net6.0\\Alien.Sharp.exe";

        var client = ServiceClient.create(ICalculator.class, buildPath(path));
        System.out.println(" *** " + client.getName() + " on JVM *** ");
        
        var value1 = 100.00D;
        var value2 = 15.99D;
        var result = client.add(value1, value2);
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