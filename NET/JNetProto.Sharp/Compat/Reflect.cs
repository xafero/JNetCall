using System;
using JNetProto.Sharp.Core;

namespace JNetProto.Sharp.Compat
{
    public static class Reflect
    {
        public static Type ToType(object obj)
        {
            var kind = DataTypes.GetKind(obj);
            try
            {
                return DataTypes.GetClass(kind.Kind);
            }
            catch (Exception)
            {
                return obj.GetType();
            }
        }
    }
}