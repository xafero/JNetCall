using System;
using System.IO;
using System.Threading;
using JNetCall.Sharp.API.Enc;
using JNetCall.Sharp.API.IO;

namespace JNetCall.Sharp.Impl.IO.Disk
{
    public sealed class FolderTransport : ISendTransport, IPushTransport, IDisposable
    {
        private readonly IEncoding<byte[]> _encoding;
        private readonly string _inputFolder;
        private readonly FileSystemWatcher _inputWatch;
        private readonly string _outputFolder;
        private readonly int _wait;

        public FolderTransport(IEncoding<byte[]> encoding, string input, string output)
        {
            _encoding = encoding;
            _inputFolder = CreateFolder(input);
            _inputWatch = StartWatch();
            _outputFolder = CreateFolder(output);
            _wait = 5;
        }

        private const string Prefix = "d_";
        private const string Suffix = ".s";

        private FileSystemWatcher StartWatch()
        {
            var watcher = new FileSystemWatcher(_inputFolder)
            {
                Filter = Prefix + "*" + Suffix,
                NotifyFilter = NotifyFilters.CreationTime
                               | NotifyFilters.DirectoryName
                               | NotifyFilters.FileName
            };
            watcher.Created += OnCreated;
            watcher.EnableRaisingEvents = true;
            return watcher;
        }

        private static string CreateFolder(string folder)
        {
            folder = Path.GetFullPath(folder);
            return Directory.Exists(folder) ? folder : Directory.CreateDirectory(folder).FullName;
        }

        private Action<string> _onPush;

        public void OnPush<T>(Action<T> data)
        {
            if (data == null)
            {
                _onPush = null;
                return;
            }
            _onPush = file => data(Get<T>(file));
        }

        private void OnCreated(object _, FileSystemEventArgs e) => _onPush?.Invoke(e.FullPath);

        private T Get<T>(string sFile)
        {
            var dFile = sFile[..^Suffix.Length];
            var msg = _encoding.Decode<T>(File.ReadAllBytes(dFile));
            ThreadPool.QueueUserWorkItem(_ =>
            {
                if (_wait >= 1)
                    Thread.Sleep(_wait);
                File.Delete(sFile);
                File.Delete(dFile);
            });
            return msg;
        }

        // ReSharper disable once StaticMemberInGenericType
        private static int _fileId;
        private static int NextId => Interlocked.Increment(ref _fileId);

        public void Send<T>(T payload)
        {
            var bytes = _encoding.Encode(payload);
            var pathData = Path.Combine(_outputFolder, Prefix + NextId);
            if (File.Exists(pathData))
                File.Delete(pathData);
            File.WriteAllBytes(pathData, bytes);
            var pathMark = pathData + Suffix;
            if (File.Exists(pathMark))
                File.Delete(pathMark);
            File.WriteAllBytes(pathMark, new byte[1]);
        }

        public void Dispose()
        {
            _onPush = null;
            _inputWatch.Created -= OnCreated;
            _inputWatch.EnableRaisingEvents = false;
            _inputWatch.Dispose();
            _encoding.Dispose();
        }
    }
}