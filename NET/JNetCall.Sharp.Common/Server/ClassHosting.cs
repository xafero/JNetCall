using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using JNetCall.Sharp.API.Flow;
using JNetCall.Sharp.API.IO;
using JNetBase.Sharp.Threads;
using JNetBase.Sharp.Meta;
using JNetCall.Sharp.Impl.Util;
using JNetCall.Sharp.Server.API;
using JNetCall.Sharp.Server.Model;

namespace JNetCall.Sharp.Server
{
    public sealed class ClassHosting : IHosting
    {
        private readonly IExecutor _executor;
        private readonly ISendTransport _protocol;
        private readonly IDictionary<string, IDictionary<string, Func<object, object[], object>>> _callMap;
        private readonly object _instance;

        private bool _running;

        public ClassHosting(object instance, ISendTransport protocol, IExecutor executor)
        {
            _executor = executor;
            _protocol = protocol;
            _callMap = new Dictionary<string, IDictionary<string, Func<object, object[], object>>>();
            _instance = instance;
        }

        public void RegisterAll()
        {
            var type = _instance.GetType();
            foreach (var infType in type.GetInterfaces())
                AddServiceEndpoint(infType);
        }
        
        public void AddServiceEndpoint<T>()
        {
            AddServiceEndpoint(typeof(T));
        }
        
        public void AddServiceEndpoint(Type interfaceClass)
        {
            var name = interfaceClass.Name.ToLowerInvariant();
            var subMap = new Dictionary<string, Func<object, object[], object>>();
            _callMap.Add(name, subMap);

            var methods = interfaceClass.GetMethods();
            foreach (var method in methods)
            {
                var methodId = ClassTools.ToMethodId(method);
                subMap.Add(methodId, (obj, args) => method.Invoke(obj, args));
            }
        }

        public void Dispose()
        {
            _running = false;
            _executor.Dispose();
            (_instance as IDisposable)?.Dispose();
            _callMap.Clear();
            _protocol.Dispose();
        }

        private static async Task<MethodResult> Pack(object res, MethodStatus status, short id)
        {
            if (res is Task task)
            {
                var taskType = task.GetType();
                var taskArg = taskType.GetGenericArguments().FirstOrDefault();
                if (taskArg == null || taskArg.Name == "VoidTaskResult")
                {
                    await task;
                    res = null;
                }
                else
                {
                    await task;
                    res = taskType.GetProperty("Result")!.GetValue(task);
                }
            }
            var obj = new MethodResult(id, res, (short)status);
            return obj;
        }

        private static Task<MethodResult> Pack(Exception e, short id)
        {
            var cause = e is TargetInvocationException ti
                ? ti.InnerException
                : e;
            var debug = cause!.ToString();
            return Pack(debug, MethodStatus.MethodFailed, id);
        }

        private Task<MethodResult> Handle(MethodCall call)
        {
            var callId = call.I;
            var callIt = call.C.ToLowerInvariant();
            if (!_callMap.ContainsKey(callIt))
            {
                var debug = call.C;
                return Pack(debug, MethodStatus.ClassNotFound, callId);
            }
            var subMap = _callMap[callIt];
            var methodIds = ClassTools.ToMethodId(call);
            if (!subMap.TryGetValue(methodIds.Item1, out var func))
                subMap.TryGetValue(methodIds.Item2, out func);
            if (func == null)
            {
                var debug = call.C + "::" + call.M;
                return Pack(debug, MethodStatus.MethodNotFound, callId);
            }
            try
            {
                var method = Reflect.GetMethod(func);
                var args = RewriteArgsIfNeeded(call.A, method.GetParameters());
                var res = func(_instance, args);
                return Pack(res, MethodStatus.Ok, callId);
            }
            catch (Exception e)
            {
                return Pack(e, callId);
            }
        }

        private static Delegate WrapToDelegate(Type prm, DelegateWrap obj)
        {
            var delMethod = prm.GetMethod("Invoke")!;
            var delReturn = delMethod.ReturnType;
            string term;
            Type[] startArgs;
            int offset;
            if (delReturn == typeof(void))
            {
                term = nameof(DelegateWrap.DynAction);
                startArgs = Type.EmptyTypes;
                offset = 0;
            }
            else
            {
                term = nameof(DelegateWrap.DynFunc);
                startArgs = new[] { delReturn };
                offset = 1;
            }
            var delArgs = startArgs.Concat(delMethod
                    .GetParameters().Select(p => p.ParameterType))
                .ToArray();
            var delBase = obj.GetType()
                .GetMethods(BindingFlags.Public | BindingFlags.Instance)
                .First(m => m.Name == term
                            && m.GetParameters().Length == delArgs.Length - offset);
            var delGen = delBase!.MakeGenericMethod(delArgs);
            return Delegate.CreateDelegate(prm, obj, delGen);
        }

        private object[] RewriteArgsIfNeeded(object[] args, ParameterInfo[] pars)
        {
            for (var i = 0; i < args.Length; i++)
            {
                var prm = pars[i].ParameterType;
                if (!Reflect.IsDelegate(prm))
                    continue;
                var delId = (short)args[i];
                args[i] = WrapToDelegate(prm, new DelegateWrap(this, delId));
            }
            return args;
        }

        public T GoDynInvoke<T>(short callId, params object[] args)
        {
            var status = (short)MethodStatus.Continue;
            var delInvoke = new MethodResult(callId, args, status);
            _protocol.Send(delInvoke);
            var type = typeof(T);
            if (type == typeof(bool))
                return (T)(object)true;
            if (type == typeof(object))
                return default;
            throw new InvalidOperationException(type.FullName);
        }

        private void Run(MethodCall msg)
        {
            Task.Run(async () =>
            {
                var res = await Handle(msg);
                _protocol.Send(res);
            });
        }

        public void Serve()
        {
            var label = GetType().Name;
            _executor.CreateThread(ServeAndWait, label + "|Serve");
        }

        public void ServeAndWait()
        {
            _running = true;
            while (_running)
                try
                {
                    if (_protocol is IPullTransport put)
                    {
                        var pulled = put.Pull<MethodCall>();
                        Run(pulled);
                    }
                    else if (_protocol is IPushTransport pst)
                    {
                        pst.OnPush<MethodCall>(Run);
                        break;
                    }
                }
                catch (InvalidOperationException)
                {
                    _running = false;
                }
                catch (ThreadInterruptedException)
                {
                    _running = false;
                }
        }
    }
}