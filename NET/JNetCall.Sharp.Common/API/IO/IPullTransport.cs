namespace JNetCall.Sharp.API.IO
{
    public interface IPullTransport : ISendTransport
    {
        T Pull<T>();
    }
}