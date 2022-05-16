package org.example.impl;

import org.example.api.ICalculator;
import org.example.api.IDataTyped;

public class CalculatorService implements ICalculator, IDataTyped {

    public double Add(double n1, double n2) {
        double result = n1 + n2;
        System.err.printf("Received Add(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Subtract(double n1, double n2) {
        double result = n1 - n2;
        System.err.printf("Received Subtract(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Multiply(double n1, double n2) {
        double result = n1 * n2;
        System.err.printf("Received Multiply(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    public double Divide(double n1, double n2) {
        double result = n1 / n2;
        System.err.printf("Received Divide(%s,%s) %n", n1, n2);
        System.err.printf("Return: %s %n", result);
        return result;
    }

    @Override
    public String ToText(boolean b1) {
    }
}