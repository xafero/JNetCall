using System;
using Castle.DynamicProxy;

namespace JNetCall.Sharp.Client
{
    public static class ClientHelper
    {
        private static readonly Lazy<ProxyGenerator> Generator;

        static ClientHelper()
        {
            Generator = new Lazy<ProxyGenerator>(() => new ProxyGenerator());
        }

        public static T Create<T>(IInterceptor interceptor) where T : class
        {
            var gen = Generator.Value;
            var proxy = gen.CreateInterfaceProxyWithoutTarget<T>(interceptor);
            return proxy;
        }
    }
}