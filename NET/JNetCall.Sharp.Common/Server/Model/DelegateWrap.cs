using JNetCall.Sharp.Server.API;

namespace JNetCall.Sharp.Server.Model
{
    internal sealed class DelegateWrap
    {
        private readonly IHosting _host;
        private readonly short _id;

        public DelegateWrap(IHosting host, short id)
        {
            _host = host;
            _id = id;
        }

        public T0 DynFunc<T0, T1, T2>(T1 a, T2 b)
            => _host.GoDynInvoke<T0>(_id, a, b);

        public void DynAction<T1, T2>(T1 a, T2 b)
            => _host.GoDynInvoke<object>(_id, a, b);
    }
}