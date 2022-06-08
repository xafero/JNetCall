namespace JNetCall.Sharp.Server
{
    public static class ServiceHosts
    {
        public static ServiceHost<T> Create<T>()
        {
            var instance = new ServiceHost<T>(typeof(T));
            return instance;
        }
    }
}