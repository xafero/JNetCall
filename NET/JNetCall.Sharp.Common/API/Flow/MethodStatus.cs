namespace JNetCall.Sharp.API.Flow
{
    public enum MethodStatus : short
    {
        Unknown = 0,

        ClassNotFound = 404,

        MethodNotFound = 406,

        MethodFailed = 500,

        Continue = 100,

        Ok = 200
    }
}