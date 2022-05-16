using System;

namespace Example.API
{
    public interface IDataTyped : IDisposable
    {
        string ToSimpleText(byte y, short s, int i, long l, float f, double d,
            bool b, char c, string t);

        string ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f, double[] d,
            bool[] b, char[] c, string[] t);
    }
}