using System;
using System.Diagnostics;
using System.Text;
using Castle.DynamicProxy;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp.Client
{
    internal sealed class JavaInterceptor : AbstractInterceptor
    {
        public JavaInterceptor(string jar) : base(jar)
        {
        }

        private Process _process;
        private ProtoConvert _convert;

        protected override void Prepare()
        {
        }

        protected override void Start()
        {
            var pwd = Environment.CurrentDirectory;
            var utf = Encoding.UTF8;
            var process = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = "java",
                    ArgumentList = { "-jar", Jar },
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
            _convert = WriteSync(process, Settings);
        }

        private static ProtoConvert WriteSync(Process process, ProtoSettings cfg)
        {
            var stdOut = process.StandardOutput.BaseStream;
            var stdIn = process.StandardInput.BaseStream;
            var convert = new ProtoConvert(stdOut, stdIn, cfg);
            const int marker = 0xEE;
            // Send flag
            stdIn.WriteByte(marker);
            stdIn.Flush();
            // Receive flag
            while (stdOut.ReadByte() != marker)
            {
            }
            // Ready!
            return convert;
        }

        protected override void Stop(int milliseconds = 250)
        {
            _process?.WaitForExit(milliseconds);
            _process?.Kill(true);

            _process?.Close();
            _process?.Dispose();
        }

        protected override string GetErrorDetails()
        {
            _process.StandardInput.Close();
            return $"{_process.StandardOutput.ReadToEnd()} " +
                   $"{_process.StandardError.ReadToEnd()}".Trim();
        }

        public override void Intercept(IInvocation invocation)
        {
            InterceptBase(invocation, _convert);
        }
    }
}