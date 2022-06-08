namespace JNetCall.Sharp.Client
{
    public static class StdIOClient
    {
        public static T Create<T>(string exe) where T : class
        {
            var interceptor = new JavaInterceptor(exe);
            return ClientHelper.Create<T>(interceptor);
        }
    }
}