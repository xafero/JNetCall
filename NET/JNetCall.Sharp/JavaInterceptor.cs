using System;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using Castle.DynamicProxy;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace JNetCall.Sharp
{
    internal sealed class JavaInterceptor : IInterceptor, IDisposable
    {
        private static readonly JsonSerializerSettings Settings = new()
        {
            Formatting = Formatting.None,
            NullValueHandling = NullValueHandling.Ignore,
            Converters = { new StringEnumConverter() }
        };

        private readonly string _jar;

        public JavaInterceptor(string jar)
        {
            _jar = jar;
            Start();
        }

        private Process _process;

        private void Start()
        {
            if (!File.Exists(_jar))
            {
                throw new FileNotFoundException("Java archive is missing!", _jar);
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
        }

        private void Stop(int milliseconds = 250)
        {
            _process?.WaitForExit(milliseconds);
            _process?.Kill(true);
        }

        private void Write(object obj)
        {
            var json = JsonConvert.SerializeObject(obj, Settings);
            _process.StandardInput.WriteLine(json);
        }

        private T Read<T>()
        {
            var json = _process.StandardOutput.ReadLine();
            try
            {
                return JsonConvert.DeserializeObject<T>(json!, Settings);
            }
            catch (Exception e)
            {
                var error = $"{json} {_process.StandardError.ReadToEnd()}".Trim();
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
                A = invocation.Arguments,
                H = invocation.Arguments.Select(Conversions.GetHint).ToArray()
            };
            if (call.C == nameof(IDisposable) && call.M == nameof(IDisposable.Dispose))
            {
                Dispose();
                return;
            }
            Write(call);
            var input = Read<MethodResult>();
            switch (input.S)
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