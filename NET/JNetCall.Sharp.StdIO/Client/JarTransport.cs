using System;
using System.Diagnostics;
using System.IO;
using System.Text;
using JNetCall.Sharp.API.IO;
using JNetCall.Sharp.Common;

namespace JNetCall.Sharp.Client
{
    internal sealed class JarTransport : ISendTransport, IPullTransport
    {
        private readonly string _jar;
        private readonly IPullTransport _parent;

        public JarTransport(string jar, StreamInit init)
        {
            if (!File.Exists(jar))
                throw new FileNotFoundException($"Missing: {jar}");
            _jar = jar;
            var (stdIn, stdOut) = Start();
            _parent = (IPullTransport)init(stdIn, stdOut);
        }

        private Process _process;

        private (Stream stdIn, Stream stdOut) Start()
        {
            var pwd = Environment.CurrentDirectory;
            var utf = Encoding.UTF8;
            var process = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = "java",
                    ArgumentList = { "-jar", _jar },
                    WorkingDirectory = pwd,
                    UseShellExecute = false,
                    StandardInputEncoding = utf,
                    RedirectStandardInput = true,
                    StandardErrorEncoding = utf,
                    RedirectStandardError = true,
                    StandardOutputEncoding = utf,
                    RedirectStandardOutput = true
                }
            };
            (_process = process).Start();
            return WriteSync(process);
        }

        private void Stop(int milliseconds)
        {
            _process?.WaitForExit(milliseconds);
            _process?.Kill(true);

            _process?.Close();
            _process?.Dispose();
        }

        private static (Stream stdIn, Stream stdOut) WriteSync(Process process)
        {
            var stdOut = process.StandardOutput.BaseStream;
            var stdIn = process.StandardInput.BaseStream;
            return ByteMarks.WriteSync(stdOut, stdIn);
        }

        private string GetErrorDetails()
        {
            _process.StandardInput.Close();
            return $"{_process.StandardOutput.ReadToEnd()} " +
                   $"{_process.StandardError.ReadToEnd()}".Trim();
        }

        public void Send<T>(T payload)
        {
            _parent.Send(payload);
        }

        public T Pull<T>()
        {
            var msg = _parent.Pull<T>();
            return msg;
        }

        public void Dispose()
        {
            Stop(250);
            _parent.Dispose();
        }
    }
}