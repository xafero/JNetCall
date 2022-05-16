namespace JNetCall.Sharp
{
    public readonly record struct MethodCall
    {
        public string C { get; init; }
        public string M { get; init; }
        public object[] A { get; init; }
    }
}