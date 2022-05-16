using System;

namespace Example.API
{
    public interface IDataTyped : IDisposable
    {
        string ToText(bool b1);
    }
}