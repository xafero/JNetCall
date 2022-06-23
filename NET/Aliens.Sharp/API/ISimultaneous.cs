using System;
using System.Threading.Tasks;

namespace Example.API
{
    public interface ISimultaneous : IDisposable
    {
        Task<int> GetId();

        Task LoadIt(string word);

        Task<string> RemoveIt();

        Task<Tuple<int, long>> RunIt(int waitMs, int idx);
    }
}
