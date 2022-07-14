namespace JNetCall.Sharp.API.Flow
{
    public readonly record struct MethodResult(short I, object R, short S) 
        : ICall;
}