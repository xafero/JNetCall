using JNetBase.Sharp.Threads;
using JNetCall.Sharp.Client.Tools;

namespace JNetCall.Sharp.Client
{
    public static class InProcClient
    {
        public static T Create<T>(string jar) where T : class
        {
            var pool = new ThreadExecutor();
            var protocol = new JvmTransport(jar);
            var handler = new ClassProxy(protocol, pool);
            handler.Listen();
            return ClientHelper.Create<T>(handler);
        }
    }
}