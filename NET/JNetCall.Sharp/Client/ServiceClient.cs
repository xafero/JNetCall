using System;
using Castle.DynamicProxy;

namespace JNetCall.Sharp.Client
{
    public static class ServiceClient
    {
        private static readonly Lazy<ProxyGenerator> Generator;

        static ServiceClient()
        {
            Generator = new Lazy<ProxyGenerator>(() => new ProxyGenerator());
        }

        public static T Create<T>(string exe) where T : class
        {
            var interceptor = new JavaInterceptor(exe);
            var gen = Generator.Value;
            var proxy = gen.CreateInterfaceProxyWithoutTarget<T>(interceptor);
            return proxy;
        }
    }
}