namespace JNetCall.Sharp
{
    public enum MethodStatus : short
    {
        Unknown = 0,

        ClassNotFound = 404,

        MethodNotFound = 406,

        MethodFailed = 500,

        Ok = 200
    }
}