using System;

namespace Example.API
{
    public interface ICalculator : IDisposable
    {
        double Add(double n1, double n2);
        double Subtract(double n1, double n2);
        double Multiply(double n1, double n2);
        double Divide(double n1, double n2);
    }
}