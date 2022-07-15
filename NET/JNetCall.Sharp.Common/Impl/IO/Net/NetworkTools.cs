using System.Linq;
using System.Net;
using System.Net.Sockets;

namespace JNetCall.Sharp.Impl.IO.Net
{
    public static class NetworkTools
    {
        public static IPEndPoint ToEndPoint(string host, int port)
        {
            var dns = Dns.GetHostEntry(host);
            var l = dns.AddressList;
            var ip = l.First(a => a.AddressFamily == AddressFamily.InterNetwork);
            var endPoint = new IPEndPoint(ip, port);
            return endPoint;
        }
    }
}