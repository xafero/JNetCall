package org.example.impl;

import org.example.api.ICalculator;

public class CalculatorService implements ICalculator {

    public double Add(double n1, double n2) {
        double result = n1 + n2;
        System.out.printf("Received Add(%s,%s) %n", n1, n2);
        System.out.printf("Return: %s %n", result);
        return result;
    }

    public double Subtract(double n1, double n2) {
        double result = n1 - n2;
        System.out.printf("Received Subtract(%s,%s) %n", n1, n2);
        System.out.printf("Return: %s %n", result);
        return result;
    }

    public double Multiply(double n1, double n2) {
        double result = n1 * n2;
        System.out.printf("Received Multiply(%s,%s) %n", n1, n2);
        System.out.printf("Return: %s %n", result);
        return result;
    }

    public double Divide(double n1, double n2) {
        double result = n1 / n2;
        System.out.printf("Received Divide(%s,%s) %n", n1, n2);
        System.out.printf("Return: %s %n", result);
        return result;
    }
}