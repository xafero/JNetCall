using System;
using System.Runtime.InteropServices;
using JNetHotel.Sharp.Interop;

namespace JNetHotel.Sharp
{
    public unsafe class Env : IDisposable
    {
        private readonly IntPtr _pointer;
        private readonly JNINativeInterface _functions;

        internal Env(IntPtr jniEnv)
        {
            _pointer = jniEnv;
            _functions = *(*(JNINativeInterfacePtr*)jniEnv.ToPointer()).functions;
        }

        public void Dispose()
        {
            GC.SuppressFinalize(this);
        }

        private JNINativeInterface_.FindClass findClass;
        private JNINativeInterface_.ExceptionCheck exceptionCheck;
        private JNINativeInterface_.GetStaticMethodId getStaticMethodId;
        private JNINativeInterface_.ExceptionOccurred exceptionOccurred;
        private JNINativeInterface_.ExceptionClear exceptionClear;
        private JNINativeInterface_.GetObjectClass getObjectClass;
        private JNINativeInterface_.GetMethodId getMethodId;
        private JNINativeInterface_.GetStringChars getStringChars;
        private JNINativeInterface_.ReleaseStringChars releaseStringChars;
        private JNINativeInterface_.CallObjectMethod callObjectMethod;
        private JNINativeInterface_.CallStaticVoidMethod callStaticVoidMethod;
        private JNINativeInterface_.GetByteArrayElements getByteArrayElements;
        private JNINativeInterface_.GetArrayLength getArrayLength;
        private JNINativeInterface_.DeleteLocalRef deleteLocalRef;
        private JNINativeInterface_.CallStaticObjectMethod callStaticObjectMethod;
        private JNINativeInterface_.GetPrimitiveArrayCritical getPrimitiveArrayCritical;
        private JNINativeInterface_.ReleasePrimitiveArrayCritical releasePrimitiveArrayCritical;
        private JNINativeInterface_.NewByteArray newByteArray;

        public IntPtr FindClass(string name)
        {
            if (findClass == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.FindClass, ref findClass);
            }
            var res = findClass.Invoke(_pointer, name);
            CheckJavaExceptionAndThrow();
            return res;
        }

        public IntPtr GetStaticMethodId(IntPtr jniClass, string name, string sig)
        {
            if (getStaticMethodId == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetStaticMethodID, ref getStaticMethodId);
            }
            var res = getStaticMethodId.Invoke(_pointer, jniClass, name, sig);
            CheckJavaExceptionAndThrow();
            return res;
        }

        public IntPtr ExceptionOccurred()
        {
            if (exceptionOccurred == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.ExceptionOccurred, ref exceptionOccurred);
            }
            var res = exceptionOccurred(_pointer);
            return res;
        }

        public string CatchJavaException()
        {
            var occurred = ExceptionOccurred();
            if (occurred != default)
            {
                ExceptionClear();
                var exceptionClass = GetObjectClass(occurred);
                var mid = GetMethodId(exceptionClass, "toString", "()Ljava/lang/String;");
                var raw = CallObjectMethod(occurred, mid, new JValue());
                return JStringToString(raw);
            }
            return string.Empty;
        }

        public IntPtr CallObjectMethod(IntPtr obj, IntPtr methodId, params JValue[] args)
        {
            if (callObjectMethod == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.CallObjectMethodA, ref callObjectMethod);
            }
            var res = callObjectMethod(_pointer, obj, methodId, args);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal IntPtr GetStringChars(IntPtr jStr, byte* b)
        {
            if (getStringChars == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetStringChars, ref getStringChars);
            }
            var res = getStringChars(_pointer, jStr, b);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal void ReleaseStringChars(IntPtr jStr, IntPtr chars)
        {
            if (releaseStringChars == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.ReleaseStringChars, ref releaseStringChars);
            }
            releaseStringChars(_pointer, jStr, chars);
            CheckJavaExceptionAndThrow();
        }

        internal string JStringToString(IntPtr jStr)
        {
            if (jStr == IntPtr.Zero) 
                return null;
            byte b;
            var chars = GetStringChars(jStr, &b);
            var result = Marshal.PtrToStringUni(chars);
            ReleaseStringChars(jStr, chars);
            return result;
        }

        public IntPtr GetMethodId(IntPtr jniClass, string name, string sig)
        {
            if (getMethodId == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetMethodID, ref getMethodId);
            }
            var res = getMethodId.Invoke(_pointer, jniClass, name, sig);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal IntPtr GetObjectClass(IntPtr obj)
        {
            if (getObjectClass == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetObjectClass, ref getObjectClass);
            }
            var jniClass = getObjectClass.Invoke(_pointer, obj);
            CheckJavaExceptionAndThrow();
            return jniClass;
        }

        public void ExceptionClear()
        {
            if (exceptionClear == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.ExceptionClear, ref exceptionClear);
            }
            exceptionClear(_pointer);
        }

        public bool CheckJavaExceptionAndThrow()
        {
            if (exceptionCheck == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.ExceptionCheck, ref exceptionCheck);
            }
            if (exceptionCheck(_pointer) != 0)
            {
                throw new Exception("ExceptionCheck() failed");
            }
            return exceptionCheck(_pointer) != 0;
        }

        public void CallStaticVoidMethod(IntPtr jniClass, IntPtr methodId, params JValue[] args)
        {
            if (callStaticVoidMethod == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.CallStaticVoidMethodA, ref callStaticVoidMethod);
            }
            callStaticVoidMethod(_pointer, jniClass, methodId, args);
            CheckJavaExceptionAndThrow();
        }

        internal byte[] JStringToByte(IntPtr jStr)
        {
            return jStr != IntPtr.Zero ? GetByteArray(jStr) : null;
        }

        public int GetArrayLength(IntPtr obj)
        {
            if (getArrayLength == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetArrayLength, ref getArrayLength);
            }
            var len = getArrayLength(_pointer, obj);
            CheckJavaExceptionAndThrow();
            return len;
        }

        internal void DeleteLocalRef(IntPtr objectHandle)
        {
            if (deleteLocalRef == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.DeleteLocalRef, ref deleteLocalRef);
            }
            if (objectHandle != default)
            {
                deleteLocalRef(_pointer, objectHandle);
            }
        }

        internal byte[] GetByteArray(IntPtr obj)
        {
            if (getByteArrayElements == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetByteArrayElements, ref getByteArrayElements);
            }
            var res = getByteArrayElements(_pointer, obj, null);
            var len = GetArrayLength(obj);
            var bResult = new byte[len];
            var byteSource = (IntPtr)res;
            Marshal.Copy(byteSource, bResult, 0, len);
            CheckJavaExceptionAndThrow();
            return bResult;
        }

        public IntPtr CallStaticObjectMethod(IntPtr obj, IntPtr methodId, params JValue[] args)
        {
            if (callStaticObjectMethod == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.CallStaticObjectMethodA, ref callStaticObjectMethod);
            }
            var res = callStaticObjectMethod(_pointer, obj, methodId, args);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal void* GetPrimitiveArrayCritical(IntPtr array, byte* isCopy)
        {
            if (getPrimitiveArrayCritical == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.GetPrimitiveArrayCritical, ref getPrimitiveArrayCritical);
            }
            var res = getPrimitiveArrayCritical(_pointer, array, isCopy);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal void ReleasePrimitiveArrayCritical(IntPtr array, void* carray, int mode)
        {
            if (releasePrimitiveArrayCritical == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.ReleasePrimitiveArrayCritical, ref releasePrimitiveArrayCritical);
            }
            releasePrimitiveArrayCritical(_pointer, array, carray, mode);
            CheckJavaExceptionAndThrow();
        }

        public IntPtr NewByteArray(int len)
        {
            if (newByteArray == null)
            {
                Jvm.GetDelegateForFunctionPointer(_functions.NewByteArray, ref newByteArray);
            }
            var res = newByteArray(_pointer, len);
            CheckJavaExceptionAndThrow();
            return res;
        }

        internal void PackPrimitiveArray<T>(T[] sourceArray, IntPtr pointerToArray)
        {
            byte isCopy = 0;
            var byteArray = new byte[sourceArray.Length * Marshal.SizeOf<T>()];
            Buffer.BlockCopy(sourceArray, 0, byteArray, 0, sourceArray.Length * Marshal.SizeOf<T>());
            var pb = (byte*)GetPrimitiveArrayCritical(pointerToArray, &isCopy);
            if (pb == null)
            {
                throw new Exception("An error occurred whilst packing the array");
            }
            try
            {
                Marshal.Copy(byteArray, 0, new IntPtr(pb), sourceArray.Length * Marshal.SizeOf<T>());
            }
            finally
            {
                ReleasePrimitiveArrayCritical(pointerToArray, pb, 0);
            }
        }
    }
}