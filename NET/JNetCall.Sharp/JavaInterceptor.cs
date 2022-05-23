using System;
using System.Diagnostics;
using System.IO;
using System.Text;
using Castle.DynamicProxy;
using JNetProto.Sharp;
using JNetProto.Sharp.Beans;

namespace JNetCall.Sharp
{
    internal sealed class JavaInterceptor : IInterceptor, IDisposable
    {
        private static readonly ProtoSettings Settings = new();

        private readonly string _jar;

        public JavaInterceptor(string jar)
        {
            _jar = jar;
            Start();
        }

        private Process _process;
        private ProtoConvert _convert;

        private void Start()
        {
            if (!File.Exists(_jar))
            {
                throw new FileNotFoundException($"Missing: {_jar}");
            }
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
            while (stdOut.ReadByte() != marker) { }
            // Ready!
            return convert;
        }

        private void Stop(int milliseconds = 250)
        {
            _process?.WaitForExit(milliseconds);
            _process?.Kill(true);
        }

        private void Write(object obj)
        {
            _convert.WriteObject(obj);
            _convert.Flush();
        }

        private T Read<T>()
        {
            try
            {
                var obj = _convert.ReadObject<T>();
                return obj;
            }
            catch (Exception e)
            {
                _process.StandardInput.Close();
                var error = $"{_process.StandardOutput.ReadToEnd()} " +
                            $"{_process.StandardError.ReadToEnd()}".Trim();
                throw new InvalidOperationException(error, e);
            }
        }

        public void Intercept(IInvocation invocation)
        {
            var method = invocation.Method;
            var call = new MethodCall
            {
                C = method.DeclaringType?.Name,
                M = method.Name,
                A = invocation.Arguments
            };
            if (call.C == nameof(IDisposable) && call.M == nameof(IDisposable.Dispose))
            {
                Dispose();
                return;
            }
            Write(call);
            var input = Read<MethodResult>();
            var status = (MethodStatus)input.S;
            switch (status)
            {
                case MethodStatus.Ok:
                    var raw = Conversions.Convert(method.ReturnType, input.R);
                    invocation.ReturnValue = raw;
                    break;
                default:
                    throw new InvalidOperationException($"[{input.S}] {input.R}");
            }
        }

        public void Dispose()
        {
            Stop();
            _process?.Close();
            _process?.Dispose();
        }
    }
}