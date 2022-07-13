using System;
using System.Linq;
using JNetProto.Sharp.API;
using JNetProto.Sharp.Core;
using JNetProto.Sharp.Tools;
using Stream = System.IO.Stream;
using MemoryStream = System.IO.MemoryStream;

namespace JNetProto.Sharp.Beans
{
    public sealed class ProtoConvert : IDisposable
    {
        private readonly object _writeLock = new();
        private readonly object _readLock = new();

        private readonly IDataReader _reader;
        private readonly IDataWriter _writer;
        private readonly ProtoSettings _cfg;

        public ProtoConvert(Stream stdOutput, Stream stdInput, ProtoSettings cfg)
        {
            _reader = new BinaryReader(stdOutput);
            _writer = new BinaryWriter(stdInput);
            _cfg = cfg;
        }

        public void WriteObject(object obj)
        {
            var bytes = SerializeObject(obj, _cfg);
            lock (_writeLock)
            {
                _writer.WriteBinary(bytes);
            }
        }

        public T ReadObject<T>()
        {
            byte[] bytes;
            lock (_readLock)
            {
                bytes = _reader.ReadBinary();
            }
            return DeserializeObject<T>(bytes, _cfg);
        }

        public void Flush()
        {
            lock (_writeLock)
            {
                _writer.Flush();
            }
        }

        public void Dispose()
        {
            _reader?.Dispose();
            _writer?.Dispose();
        }

        private static byte[] SerializeObject(object obj, ProtoSettings s)
        {
            var raw = Conversions.ToObjectArray(obj);
            var args = (object[])raw;
            return SerializeObject(args, s);
        }

        private static T DeserializeObject<T>(object[] args, ProtoSettings _)
        {
            var type = typeof(T);
            var raw = Conversions.FromObjectArray(type, args);
            return (T)raw;
        }

        private static byte[] SerializeObject(object[] args, ProtoSettings _)
        {
            using var mem = new MemoryStream();
            using IDataWriter writer = new BinaryWriter(mem);
            writer.WriteObject(args);
            return mem.ToArray();
        }

        private static T DeserializeObject<T>(byte[] bytes, ProtoSettings s)
        {
            using var mem = new MemoryStream(bytes);
            using IDataReader reader = new BinaryReader(mem);
            var args = (object[])reader.ReadObject();
            return DeserializeObject<T>(args, s);
        }
    }
}
