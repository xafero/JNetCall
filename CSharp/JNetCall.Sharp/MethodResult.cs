namespace JNetCall.Sharp
{
    public readonly record struct MethodResult
    {
        public object R { get; init; }
        public MethodStatus S { get; init; }
    }
}