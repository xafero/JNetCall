namespace JNetCall.Sharp.Client
{
    public static class InProcClient
    {
        public static T Create<T>(string exe) where T : class
        {
            var interceptor = new JvmInterceptor(exe);
            return ClientHelper.Create<T>(interceptor);
        }
    }
}