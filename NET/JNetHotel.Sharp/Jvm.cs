using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.API;
using JNetHotel.Sharp.Data;
using JNetHotel.Sharp.Interop;

namespace JNetHotel.Sharp
{
    public sealed unsafe class Jvm : IDisposable
    {
        public Jvm(IVmRef vmRef, string jar) : this(CreateVm(jar, vmRef))
        {
        }

        private static (IntPtr, Env) CreateVm(string jar, IVmRef vmRef)
        {
            var opts = BuildOptions(jar);
            return LoadVm(opts, vmRef);
        }

        private IntPtr _jvm;
        private readonly Env _env;
        private readonly JNIInvokeInterface _functions;

        private JNIInvokeInterface_.DestroyJavaVM _destroyJavaVm;
        private JNIInvokeInterface_.DetachCurrentThread _detachCurrentThread;
        private JNIInvokeInterface_.AttachCurrentThread _attachCurrentThread;

        private Jvm((IntPtr jvm, Env env) tuple)
        {
            _jvm = tuple.jvm;
            _env = tuple.env;
            _functions = *(*(JNIInvokeInterfacePtr*)_jvm.ToPointer()).functions;
        }

        public static byte BooleanToByte(bool value) => value ? (byte)1 : (byte)0;

        private static (IntPtr, Env) LoadVm(IDictionary<string, string> options, IVmRef vmRef)
        {
            var args = new JvmInitArgs
            {
                version = 0x00010006,
                ignoreUnrecognized = BooleanToByte(true)
            };
            if (options.Count > 0)
            {
                args.nOptions = options.Count;
                var opt = new JvmOption[options.Count];
                var i = 0;
                foreach (var kvp in options)
                    opt[i++].optionString = Marshal.StringToHGlobalAnsi($"{kvp.Key}={kvp.Value}");
                fixed (JvmOption* a = &opt[0])
                {
                    args.options = a;
                }
            }
            var attached = AttachToCurrentJvm(args, vmRef);
            if (attached == null)
                return CreateJvm(vmRef, args);
            return attached.Value;
        }

        private static (IntPtr jvm, Env env) CreateJvm(IVmRef vmRef, JvmInitArgs args)
        {
            var result = vmRef.CreateVm(out var jvmPointer, out var environment, &args);
            if (result != JniResult.Success)
            {
                throw new Exception($"Cannot create JVM: {result}");
            }
            var env = new Env(environment);
            return (jvmPointer, env);
        }

        internal int AttachCurrentThread(out Env pEnv, JvmInitArgs? args)
        {
            if (_attachCurrentThread == null)
            {
                GetDelegateForFunctionPointer(_functions.AttachCurrentThread, ref _attachCurrentThread);
            }
            IntPtr env;
            int result;
            if (args.HasValue)
            {
                var initArgs = args.Value;
                result = _attachCurrentThread.Invoke(_jvm, out env, &initArgs);
            }
            else
            {
                result = _attachCurrentThread.Invoke(_jvm, out env, null);
            }
            pEnv = new Env(env);
            return result;
        }

        private static (IntPtr, Env)? AttachToCurrentJvm(JvmInitArgs args, IVmRef vmRef)
        {
            var res = vmRef.GetCreatedVms(out var javaVirtualMachine, 1, out var nVMs);
            if (res != JniResult.Success)
                throw new Exception($"JNI_GetCreatedJavaVMs failed: {res}");
            if (nVMs <= 0)
                return null;
            var jvm = new Jvm((javaVirtualMachine, null));
            var actRes = jvm.AttachCurrentThread(out var env, args);
            if (actRes != (int)JniResult.Success)
                throw new Exception($"AttachCurrentThread failed: {res}");
            return (javaVirtualMachine, env);
        }

        private static IDictionary<string, string> BuildOptions(string jarFile)
        {
            var jvmParams = new Dictionary<string, string>
            {
                { "-Djava.class.path", string.Join(":", new List<string> { jarFile }) }
            };
            return jvmParams;
        }

        public static void GetDelegateForFunctionPointer<T>(IntPtr ptr, ref T res)
        {
            res = Marshal.GetDelegateForFunctionPointer<T>(ptr);
        }

        private int DestroyJavaVm()
        {
            if (_destroyJavaVm == null)
            {
                GetDelegateForFunctionPointer(_functions.DestroyJavaVM, ref _destroyJavaVm);
            }
            return _destroyJavaVm.Invoke(_jvm);
        }

        private int DetachCurrentThread()
        {
            if (_detachCurrentThread == null)
            {
                GetDelegateForFunctionPointer(_functions.DetachCurrentThread, ref _detachCurrentThread);
            }
            return _detachCurrentThread.Invoke(_jvm);
        }

        public void Dispose()
        {
            if (_jvm == IntPtr.Zero)
                return;
            DetachCurrentThread();
            if (!AppDomain.CurrentDomain.FriendlyName.StartsWith("ReSharper"))
                DestroyJavaVm();
            _jvm = IntPtr.Zero;
            GC.SuppressFinalize(this);
        }

        ~Jvm()
        {
            Dispose();
        }

        public void CallStaticVoidMethod(string className, string methodName, string sig, List<object> param)
        {
            var javaObject = _env.FindClass(className);
            var methodId = _env.GetStaticMethodId(javaObject, methodName, sig);
            try
            {
                _env.CallStaticVoidMethod(javaObject, methodId, ParseParameters(sig, param));
            }
            catch
            {
                throw new Exception(_env.CatchJavaException());
            }
        }

        private JValue[] ParseParameters(string sig, IList param)
        {
            var retVal = new JValue[param.Count];
            var startIndex = sig.IndexOf('(') + 1;
            for (var i = 0; i < param.Count; i++)
            {
                var paramSig = "";
                if (sig.Substring(startIndex, 1) == "[")
                    paramSig = sig.Substring(startIndex++, 1);

                if (sig.Substring(startIndex, 1) != "L")
                    paramSig += sig.Substring(startIndex, 1);

                startIndex += paramSig.Length - (paramSig.IndexOf("[", StringComparison.Ordinal) + 1);

                if (param[i] == null)
                {
                    retVal[i] = new JValue();
                }
                else if (paramSig.StartsWith("["))
                {
                    retVal[i] = ProcessArrayType(paramSig, param[i]);
                }
            }
            return retVal;
        }

        private JValue ProcessArrayType(string paramSig, object param)
        {
            IntPtr arrPointer;
            if (paramSig.Equals("[B"))
            {
                arrPointer = _env.NewByteArray(((Array)param).Length);
            }
            else
            {
                throw new Exception($"Signature ({paramSig}) does not match parameter value {param.GetType()}");
            }
            if (param is byte[] bytes)
            {
                _env.PackPrimitiveArray(bytes, arrPointer);
            }
            return new JValue { l = arrPointer };
        }

        public T CallStaticMethod<T>(string className, string methodName, string sig, List<object> param)
        {
            var javaObject = _env.FindClass(className);
            var methodId = _env.GetStaticMethodId(javaObject, methodName, sig);
            try
            {
                if (typeof(T) == typeof(byte[]))
                {
                    var obj = _env.CallStaticObjectMethod(javaObject, methodId, ParseParameters(sig, param));
                    if (obj == IntPtr.Zero)
                    {
                        return default;
                    }
                    var res = _env.JStringToByte(obj);
                    _env.DeleteLocalRef(obj);
                    return (T)(object)res;
                }
                return default;
            }
            catch
            {
                throw new Exception(_env.CatchJavaException());
            }
        }
    }
}