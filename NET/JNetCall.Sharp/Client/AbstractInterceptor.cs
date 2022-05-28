﻿using System;
using System.IO;
using Castle.DynamicProxy;
using JNetCall.Sharp.API;
using JNetProto.Sharp.Beans;
using JNetProto.Sharp.Tools;

namespace JNetCall.Sharp.Client
{
    internal abstract class AbstractInterceptor : IInterceptor, IDisposable
    {
        protected static readonly ProtoSettings Settings = new();

        protected readonly string Jar;

        protected AbstractInterceptor(string jar)
        {
            Jar = jar;
            StartBase();
        }

        private void StartBase()
        {
            Prepare();
            if (!File.Exists(Jar))
            {
                throw new FileNotFoundException($"Missing: {Jar}");
            }
            Start();
        }

        protected abstract void Prepare();
        protected abstract void Start();
        protected abstract void Stop(int milliseconds = 250);
        protected abstract string GetErrorDetails();

        public abstract void Intercept(IInvocation invocation);

        protected MethodCall? Pack(IInvocation invocation)
        {
            var method = invocation.Method;
            var call = new MethodCall
            {
                C = method.DeclaringType?.Name,
                M = method.Name,
                A = invocation.Arguments
            };
            if (call.C == nameof(IDisposable) && call.M == nameof(IDisposable.Dispose))
            {
                Dispose();
                return null;
            }
            return call;
        }

        protected void Unpack(IInvocation invocation, MethodResult input)
        {
            var method = invocation.Method;
            var status = (MethodStatus)input.S;
            switch (status)
            {
                case MethodStatus.Ok:
                    var raw = Conversions.Convert(method.ReturnType, input.R);
                    invocation.ReturnValue = raw;
                    break;
                default:
                    throw new InvalidOperationException($"[{input.S}] {input.R}");
            }
        }

        protected void InterceptBase(IInvocation invocation, ProtoConvert proto)
        {
            var call = Pack(invocation);
            if (call == null)
                return;
            Write(proto, call);
            var answer = Read<MethodResult>(proto);
            Unpack(invocation, answer);
        }

        protected T Read<T>(ProtoConvert convert)
        {
            try
            {
                var obj = convert.ReadObject<T>();
                return obj;
            }
            catch (Exception e)
            {
                var error = GetErrorDetails();
                throw new InvalidOperationException(error, e);
            }
        }

        protected static void Write(ProtoConvert convert, object obj)
        {
            convert.WriteObject(obj);
            convert.Flush();
        }

        public void Dispose()
        {
            Stop();
        }
    }
}