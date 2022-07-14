namespace JNetCall.Sharp.API.Flow
{
    public readonly record struct MethodCall(short I, string C, string M, object[] A) 
        : ICall;
}