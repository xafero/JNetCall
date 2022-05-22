namespace JNetCall.Sharp
{
    public readonly record struct MethodCall(
        string C, string M, object[] A);
}