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

        public static T CreateNative<T>(string exe) where T : class
        {
            var interceptor = new JvmInterceptor(exe);
            return Create<T>(interceptor);
        }

        public static T CreateMain<T>(string exe) where T : class
        {
            var interceptor = new JavaInterceptor(exe);
            return Create<T>(interceptor);
        }

        private static T Create<T>(IInterceptor interceptor) where T : class
        {
            var gen = Generator.Value;
            var proxy = gen.CreateInterfaceProxyWithoutTarget<T>(interceptor);
            return proxy;
        }
    }
}