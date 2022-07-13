using System;

namespace Example.API
{
    public interface IStringCache : IDisposable
    {
        void Set(int key, string value);
        string Get(int key);
        void Delete(int key);
        int Size { get; }

        void Close();
    }
}
