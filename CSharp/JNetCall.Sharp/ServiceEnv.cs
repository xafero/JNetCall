using System;
using System.IO;

namespace JNetCall.Sharp
{
    public static class ServiceEnv
    {
        public static string GetBaseFolder()
        {
            var baseDir = AppContext.BaseDirectory;
            var sep = Path.DirectorySeparatorChar;
            var parts = new[] { "net6.0", "Debug", "Release", "bin" };
            foreach (var part in parts)
            {
                var tmp = sep + part + sep;
                if (!baseDir.EndsWith(tmp))
                    continue;
                baseDir = baseDir[..^tmp.Length] + sep;
            }
            return baseDir;
        }

        public static string BuildPath(string path)
        {
            var baseDir = GetBaseFolder();
            var destDir = Path.Combine(baseDir, path);
            var target = Path.GetFullPath(destDir);
            return target;
        }
    }
}