using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using Castle.DynamicProxy;
using JNetCall.Sharp.API.Flow;
using JNetCall.Sharp.API.IO;
using JNetBase.Sharp.Threads;
using JNetCall.Sharp.Client.API;
using JNetCall.Sharp.Client.Model;
using JNetBase.Sharp.Meta;
using JNetCall.Sharp.Impl.Util;
using JNetProto.Sharp.Tools;
using Nito.AsyncEx;

namespace JNetCall.Sharp.Client
{
    public sealed class ClassProxy : IProxy
    {
        private readonly IExecutor _executor;
        private readonly ISendTransport _protocol;
        private readonly ConcurrentDictionary<short, CallState> _signals;

        private bool _running;

        public ClassProxy(ISendTransport protocol, IExecutor executor)
        {
            _executor = executor;
            _protocol = protocol;
            _signals = new ConcurrentDictionary<short, CallState>();
        }

        public void Intercept(IInvocation invocation)
        {
            var call = Pack(invocation);
            if (call == null)
                return;
            var answer = Request(invocation.Method, call.Value);
            invocation.ReturnValue = answer;
        }

        private static int _callId;
        private static int NextId => Interlocked.Increment(ref _callId);

        private MethodCall? Pack(IInvocation invocation)
        {
            var method = invocation.Method;
            var id = (short)NextId;
            var call = new MethodCall
            {
                I = id,
                C = method.DeclaringType?.Name,
                M = method.Name,
                A = RewriteArgsIfNeeded(invocation.Arguments)
            };
            if (call.C == nameof(IDisposable) && call.M == nameof(IDisposable.Dispose))
            {
                Dispose();
                return null;
            }
            return call;
        }

        private static readonly IDictionary<string, DelegateRef> Delegates
            = new Dictionary<string, DelegateRef>();

        private static short WrapFromDelegate(Delegate del)
        {
            var delId = ClassTools.ToDelegateId(del);
            if (!Delegates.TryGetValue(delId, out var delRef))
            {
                Delegates[delId] = delRef = new DelegateRef
                {
                    CallId = (short)NextId,
                    Entry = del
                };
            }
            return delRef.CallId;
        }

        private static object[] RewriteArgsIfNeeded(object[] args)
        {
            for (var i = 0; i < args.Length; i++)
            {
                var arg = args[i];
                if (arg is not Delegate del)
                    continue;
                args[i] = WrapFromDelegate(del);
            }
            return args;
        }

        public void Dispose()
        {
            _running = false;
            _executor.Dispose();
            DisposeSignals();
            _protocol.Dispose();
        }

        private void DisposeSignals()
        {
            foreach (var signal in _signals.ToArray())
                DisposeSignal(signal);
            _signals.Clear();
        }

        private static void DisposeSignal(KeyValuePair<short, CallState> signal)
        {
            var state = signal.Value;
            state.Result = new ThreadInterruptedException(nameof(Dispose));
            state.Set();
        }

        private CallState CreateState(ICall call, bool sync)
        {
            var callId = call.I;
            var state = new CallState();
            if (sync)
                state.SyncWait = new ManualResetEvent(false);
            else
                state.AsyncWait = new AsyncManualResetEvent(false);
            _signals[callId] = state;
            return state;
        }

        private object WaitSignal(ICall call)
        {
            var callId = call.I;
            var state = _signals[callId];
            state.SyncWait.WaitOne();
            return state.Result;
        }

        private async Task<object> PinSignal(ICall call)
        {
            var callId = call.I;
            var state = _signals[callId];
            await state.AsyncWait.WaitAsync();
            return state.Result;
        }

        private void SetSignal(MethodResult call)
        {
            if (call is { S: (short)MethodStatus.Continue } mr)
            {
                SetDelegate(mr);
                return;
            }
            var callId = call.I;
            var state = _signals[callId];
            state.Result = call;
            state.Set();
        }

        private static void SetDelegate(MethodResult msg)
        {
            var callId = msg.I;
            var state = Delegates.First(d => d.Value.CallId == callId);
            var @delegate = state.Value.Entry;
            var args = (object[])msg.R;
            var pars = @delegate.Method.GetParameters();
            for (var i = 0; i < args.Length; i++)
            {
                var par = pars[i].ParameterType;
                if (par == typeof(object))
                    continue;
                var arg = args[i];
                if (arg is not object[] oa || par == typeof(object[]))
                    continue;
                args[i] = Conversions.FromObjectArray(par, oa);
            }
            @delegate.DynamicInvoke(args);
        }

        public object Request(MethodInfo method, MethodCall call)
        {
            object answer;
            if (Reflect.IsAsync(method))
                answer = RequestAsync(method, call);
            else
                answer = RequestSync(method, call);
            return answer;
        }

        private object RequestSync(MethodInfo method, MethodCall msg)
        {
            _ = CreateState(msg, true);
            _protocol.Send(msg);
            var raw = WaitSignal(msg);
            var res = Extract(raw, method.ReturnType);
            return res;
        }

        private static Task<T> ContinueLater<T>(Task<object> task)
        {
            return task.ContinueWith(previous =>
            {
                var raw = previous.Result;
                var res = Extract(raw, typeof(T));
                return (T)res;
            });
        }

        private static object Extract(object res, Type returnType)
        {
            if (res is MethodResult mr)
            {
                return Unpack(returnType, mr);
            }
            throw new InvalidOperationException(res + " ?!");
        }

        private static readonly MethodInfo ContinueFunc = typeof(ClassProxy).GetMethod(
            nameof(ContinueLater), BindingFlags.Static | BindingFlags.NonPublic);

        private object RequestAsync(MethodInfo method, MethodCall msg)
        {
            _ = CreateState(msg, false);
            _protocol.Send(msg);
            var task = PinSignal(msg);
            var taskType = Reflect.GetTaskType(method.ReturnType);
            var func = ContinueFunc.MakeGenericMethod(taskType);
            var next = func.Invoke(null, new object[] { task });
            return next;
        }

        private static object Unpack(Type returnType, MethodResult input)
        {
            var status = (MethodStatus)input.S;
            switch (status)
            {
                case MethodStatus.Ok:
                    var raw = GetCompatibleValue(returnType, input.R);
                    return raw;
                default:
                    throw new InvalidOperationException($"[{input.S}] {input.R}");
            }
        }

        private static object GetCompatibleValue(Type retType, object retVal)
        {
            return Conversions.Convert(retType, retVal);
        }

        public void Listen()
        {
            var label = GetType().Name;
            _executor.CreateThread(ListenAndWait, label + "|Listen");
        }

        private void ListenAndWait()
        {
            _running = true;
            while (_running)
                try
                {
                    if (_protocol is IPullTransport put)
                    {
                        var pulled = put.Pull<MethodResult>();
                        SetSignal(pulled);
                    }
                    else if (_protocol is IPushTransport pst)
                    {
                        pst.OnPush<MethodResult>(SetSignal);
                        break;
                    }
                }
                catch (Exception)
                {
                    _running = false;
                }
        }
    }
}