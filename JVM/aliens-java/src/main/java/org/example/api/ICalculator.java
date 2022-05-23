package org.example.api;

public interface ICalculator extends AutoCloseable
{
    double add(double n1, double n2);
    double subtract(double n1, double n2);
    double multiply(double n1, double n2);
    double divide(double n1, double n2);
    String getName();
}