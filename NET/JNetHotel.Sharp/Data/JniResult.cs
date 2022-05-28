// ReSharper disable UnusedMember.Global
namespace JNetHotel.Sharp.Data
{
    public enum JniResult
    {
        Success = 0,
        Error = -1,
        ThreadDetached = -2,
        VersionError = -3,
        NotEnoughMemory = -4,
        AlreadyExists = -5,
        InvalidArguments = -6
    }
}