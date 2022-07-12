using System;
using System.Linq;
using JNetProto.Sharp.API;
using JNetProto.Sharp.Core;
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
            var type = obj.GetType();
            var props = type.GetProperties();
            var args = new object[props.Length];
            for (var i = 0; i < args.Length; i++)
                args[i] = props[i].GetValue(obj);
            return SerializeObject(args, s);
        }

        private static T DeserializeObject<T>(object[] args, ProtoSettings _)
        {
            var type = typeof(T);
            var cTypes = args.Select(DataTypes.ToType).ToArray();
            var creator = type.GetConstructor(cTypes);
            if (creator == null)
            {
                creator = type.GetConstructors().FirstOrDefault();
                if (creator == null)
                    throw new ArgumentException($"No constructor: {type}");
            }
            return (T)creator.Invoke(args);
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
