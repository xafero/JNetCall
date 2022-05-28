// ReSharper disable InconsistentNaming
using System;
using System.Runtime.InteropServices;

namespace JNetHotel.Sharp.Interop
{
    internal unsafe struct JNINativeInterface_
    {
        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr AllocObject(IntPtr EnvironmentHandle, IntPtr jniClass);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallBooleanMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodID, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallByteMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort CallCharMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double CallDoubleMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float CallFloatMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int CallIntMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodID, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long CallLongMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr CallObjectMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short CallShortMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void CallVoidMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallNonvirtualBooleanMethod(
            IntPtr obj, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallNonvirtualByteMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort CallNonvirtualCharMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double CallNonvirtualDoubleMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float CallNonvirtualFloatMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int CallNonvirtualIntMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long CallNonvirtualLongMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr CallNonvirtualObjectMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args
        );

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short CallNonvirtualShortMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void CallNonvirtualVoidMethod(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallStaticBooleanMethod(IntPtr EnvironmentHandle, IntPtr jniClass,
            IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte CallStaticByteMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort CallStaticCharMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double CallStaticDoubleMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float CallStaticFloatMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int CallStaticIntMethod(
            IntPtr EnvironmentHandle, IntPtr obj, IntPtr jMethodID, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long CallStaticLongMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr CallStaticObjectMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short CallStaticShortMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int CallStaticVoidMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodID, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr DefineClass(IntPtr EnvironmentHandle, IntPtr name, IntPtr loader, IntPtr buf, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void DeleteGlobalRef(IntPtr EnvironmentHandle, IntPtr gref);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void DeleteLocalRef(IntPtr EnvironmentHandle, IntPtr lref);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void DeleteWeakGlobalRef(IntPtr EnvironmentHandle, IntPtr wref);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int EnsureLocalCapacity(IntPtr EnvironmentHandle, int capacity);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte ExceptionCheck(IntPtr EnvironmentHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ExceptionClear(IntPtr EnvironmentHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ExceptionDescribe(IntPtr EnvironmentHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr ExceptionOccurred(IntPtr EnvironmentHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void FatalError(IntPtr EnvironmentHandle, IntPtr msg);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr FindClass(IntPtr EnvironmentHandle, [MarshalAs(UnmanagedType.LPStr)] string name);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetSuperclass(IntPtr EnvironmentHandle, IntPtr subclassHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte IsAssignableFrom(IntPtr EnvironmentHandle, IntPtr subclassHandle, IntPtr superclassHandle
        );

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr FromReflectedField(IntPtr EnvironmentHandle, IntPtr field);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr FromReflectedMethod(IntPtr EnvironmentHandle, IntPtr method);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetArrayLength(IntPtr EnvironmentHandle, IntPtr array);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte* GetBooleanArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetBooleanArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, byte* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte GetBooleanField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte* GetByteArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetByteArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len,
            byte* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte GetByteField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort* GetCharArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetCharArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len,
            char* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort GetCharField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetDirectBufferAddress(IntPtr EnvironmentHandle, IntPtr buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long GetDirectBufferCapacity(IntPtr EnvironmentHandle, IntPtr buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double* GetDoubleArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetDoubleArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, double* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double GetDoubleField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetFieldID(
            IntPtr EnvironmentHandle, IntPtr jniClass, [MarshalAs(UnmanagedType.LPStr)] string name,
            [MarshalAs(UnmanagedType.LPStr)] string sig);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float* GetFloatArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetFloatArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, float* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float GetFloatField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int* GetIntArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetIntArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len, int* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetIntField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetJavaVM(IntPtr EnvironmentHandle, out IntPtr vm);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long* GetLongArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetLongArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, long* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long GetLongField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetMethodId(
            IntPtr EnvironmentHandle, IntPtr jniClass, [MarshalAs(UnmanagedType.LPStr)] string name,
            [MarshalAs(UnmanagedType.LPStr)] string sig);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetObjectArrayElement(IntPtr EnvironmentHandle, IntPtr array, int index);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetObjectClass(IntPtr EnvironmentHandle, IntPtr obj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetObjectField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void* GetPrimitiveArrayCritical(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short* GetShortArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetShortArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, short* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short GetShortField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte GetStaticBooleanField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte GetStaticByteField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate ushort GetStaticCharField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate double GetStaticDoubleField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStaticFieldID(IntPtr EnvironmentHandle, IntPtr jniClass,
            [MarshalAs(UnmanagedType.LPStr)] string name, [MarshalAs(UnmanagedType.LPStr)] string sig);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate float GetStaticFloatField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetStaticIntField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate long GetStaticLongField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStaticMethodId(IntPtr EnvironmentHandle, IntPtr jniClass,
            [MarshalAs(UnmanagedType.LPStr)] string name,
            [MarshalAs(UnmanagedType.LPStr)] string sig);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStaticObjectField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate short GetStaticShortField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStringChars(IntPtr EnvironmentHandle, IntPtr str, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStringCritical(IntPtr EnvironmentHandle, IntPtr str, byte* isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetStringLength(IntPtr EnvironmentHandle, IntPtr str);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetStringRegion(IntPtr EnvironmentHandle, IntPtr str, int start, int len, char* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr GetStringUTFChars(IntPtr EnvironmentHandle, IntPtr str, IntPtr isCopy);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetStringUTFLength(IntPtr EnvironmentHandle, IntPtr str);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void GetStringUTFRegion(IntPtr EnvironmentHandle, IntPtr str, int start, int len, char* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int GetVersion(IntPtr EnvironmentHandle);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate byte IsSameObject(IntPtr EnvironmentHandle, IntPtr o1, IntPtr o2);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int MonitorEnter(IntPtr EnvironmentHandle, IntPtr obj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int MonitorExit(IntPtr EnvironmentHandle, IntPtr obj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewBooleanArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewByteArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewCharArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewDirectByteBuffer(IntPtr EnvironmentHandle, IntPtr address, long capacity);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewDoubleArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewFloatArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewGlobalRef(IntPtr EnvironmentHandle, IntPtr lobj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewIntArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewLocalRef(IntPtr EnvironmentHandle, IntPtr reference);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewLongArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewObject(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, params JValue[] args);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewObjectArray(IntPtr EnvironmentHandle, int len, IntPtr jniClass, IntPtr init);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewShortArray(IntPtr EnvironmentHandle, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewString(
            IntPtr EnvironmentHandle, [MarshalAs(UnmanagedType.LPWStr)] string unicode, int len);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewStringUTF(IntPtr EnvironmentHandle, IntPtr utf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr NewWeakGlobalRef(IntPtr EnvironmentHandle, IntPtr obj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr PopLocalFrame(IntPtr EnvironmentHandle, IntPtr result);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int PushLocalFrame(IntPtr EnvironmentHandle, int capacity);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int RegisterNatives(
            IntPtr EnvironmentHandle, IntPtr jniClass, JNINativeMethod* methods, int nMethods);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int UnregisterNatives(IntPtr EnvironmentHandle, IntPtr jniClass);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseBooleanArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* elems, int mode
        );

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseByteArrayElements(IntPtr EnvironmentHandle, IntPtr array, byte* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void
            ReleaseCharArrayElements(IntPtr EnvironmentHandle, IntPtr array, ushort* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseDoubleArrayElements(
            IntPtr EnvironmentHandle, IntPtr array, double* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void
            ReleaseFloatArrayElements(IntPtr EnvironmentHandle, IntPtr array, float* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseIntArrayElements(IntPtr EnvironmentHandle, IntPtr array, int* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseLongArrayElements(IntPtr EnvironmentHandle, IntPtr array, long* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleasePrimitiveArrayCritical(
            IntPtr EnvironmentHandle, IntPtr array, void* carray, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void
            ReleaseShortArrayElements(IntPtr EnvironmentHandle, IntPtr array, short* elems, int mode);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseStringChars(IntPtr EnvironmentHandle, IntPtr str, IntPtr chars);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseStringCritical(IntPtr EnvironmentHandle, IntPtr str, IntPtr cstring);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void ReleaseStringUTFChars(IntPtr EnvironmentHandle, IntPtr str, IntPtr chars);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetBooleanArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, byte* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetBooleanField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, byte val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetByteArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len,
            byte* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetByteField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, byte val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetCharArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len,
            char* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetCharField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, ushort val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetDoubleArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, double* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetDoubleField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, double val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetFloatArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, float* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetFloatField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, float val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetIntArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len, int* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetIntField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, int val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetLongArrayRegion(IntPtr EnvironmentHandle, IntPtr array, int start, int len,
            long* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetLongField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, long val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetObjectArrayElement(IntPtr EnvironmentHandle, IntPtr array, int index, IntPtr val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetObjectField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, IntPtr val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetShortArrayRegion(
            IntPtr EnvironmentHandle, IntPtr array, int start, int len, short* buf);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetShortField(IntPtr EnvironmentHandle, IntPtr obj, IntPtr jFieldId, short val);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticBooleanField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, byte value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticByteField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, byte value
        );

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticCharField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, ushort value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticDoubleField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, double value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticFloatField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, float value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticIntField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, int value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticLongField(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId,
            long value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticObjectField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, IntPtr value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate void SetStaticShortField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, short value);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int Throw(IntPtr EnvironmentHandle, IntPtr obj);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int ThrowNew(IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr msg);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr ToReflectedField(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jFieldId, byte isStatic);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate IntPtr ToReflectedMethod(
            IntPtr EnvironmentHandle, IntPtr jniClass, IntPtr jMethodId, byte isStatic);

        [UnmanagedFunctionPointer(CallingConvention.Winapi)]
        internal delegate int UnregisterJavaPtrs(IntPtr EnvironmentHandle, IntPtr jniClass);
    }
}