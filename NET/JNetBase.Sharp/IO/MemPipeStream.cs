using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using Nito.AsyncEx;

namespace JNetBase.Sharp.IO
{
    public class MemPipeStream : Stream, IRewindable
    {
        private readonly Stream _stream;
        private readonly AsyncProducerConsumerQueue<bool> _queue;

        public MemPipeStream(Stream stream)
        {
            _stream = stream;
            _queue = new AsyncProducerConsumerQueue<bool>();
        }

        public MemPipeStream() : this(new MemoryStream())
        {
        }

        private int _readPos;
        private int _writePos;

        public override int ReadByte()
        {
            var res = _stream.ReadByte();
            return res;
        }

        public override IAsyncResult BeginRead(byte[] buffer, int offset, int count, AsyncCallback callback,
            object state)
        {
            var res = _stream.BeginRead(buffer, offset, count, callback, state);
            return res;
        }

        public override void CopyTo(Stream destination, int bufferSize)
        {
            _stream.CopyTo(destination, bufferSize);
        }

        public override IAsyncResult BeginWrite(byte[] buffer, int offset, int count, AsyncCallback callback,
            object state)
        {
            var res = _stream.BeginWrite(buffer, offset, count, callback, state);
            return res;
        }

        public override bool CanTimeout => _stream.CanTimeout;

        public override Task CopyToAsync(Stream destination, int bufferSize, CancellationToken token)
        {
            var res = _stream.CopyToAsync(destination, bufferSize, token);
            return res;
        }

        public override Task FlushAsync(CancellationToken token)
        {
            var res = _stream.FlushAsync(token);
            return res;
        }

        public override int ReadTimeout
        {
            get => _stream.ReadTimeout;
            set => _stream.ReadTimeout = value;
        }

        public override int WriteTimeout
        {
            get => _stream.WriteTimeout;
            set => _stream.WriteTimeout = value;
        }

        public override void EndWrite(IAsyncResult asyncResult)
        {
            _stream.EndWrite(asyncResult);
        }

        public override int EndRead(IAsyncResult asyncResult)
        {
            var res = _stream.EndRead(asyncResult);
            return res;
        }

        public override ValueTask DisposeAsync()
        {
            var res = _stream.DisposeAsync();
            return res;
        }

        public override ValueTask WriteAsync(ReadOnlyMemory<byte> buffer,
            CancellationToken token = new())
        {
            var res = _stream.WriteAsync(buffer, token);
            return res;
        }

        public override async ValueTask<int> ReadAsync(Memory<byte> buffer,
            CancellationToken token = new())
        {
            await _queue.DequeueAsync(token);
            Position = _readPos;
            var res = await _stream.ReadAsync(buffer, token);
            _readPos += res;
            return res;
        }

        public override async Task WriteAsync(byte[] buffer, int offset, int count, CancellationToken token)
        {
            Position = _writePos;
            await _stream.WriteAsync(buffer, offset, count, token);
            _writePos += count;
        }

        public override async Task<int> ReadAsync(byte[] buffer, int offset, int count, CancellationToken token)
        {
            Position = _readPos;
            var res = await _stream.ReadAsync(buffer, offset, count, token);
            _readPos += res;
            return res;
        }

        public override void Write(ReadOnlySpan<byte> buffer)
        {
            _stream.Write(buffer);
        }

        public override void WriteByte(byte value)
        {
            _stream.WriteByte(value);
        }

        public override int Read(Span<byte> buffer)
        {
            _queue.Dequeue();
            Position = _readPos;
            var res = _stream.Read(buffer);
            _readPos += res;
            return res;
        }

        public override void Flush()
        {
            _stream.Flush();
        }

        public override int Read(byte[] buffer, int offset, int count)
        {
            Position = _readPos;
            var res = _stream.Read(buffer, offset, count);
            _readPos += res;
            return res;
        }

        public override long Seek(long offset, SeekOrigin origin)
        {
            var res = _stream.Seek(offset, origin);
            return res;
        }

        public override void SetLength(long value)
        {
            _stream.SetLength(value);
        }

        public override void Write(byte[] buffer, int offset, int count)
        {
            Position = _writePos;
            _stream.Write(buffer, offset, count);
            _writePos += count;
        }

        public override bool CanRead => _stream.CanRead;

        public override bool CanSeek => _stream.CanSeek;

        public override bool CanWrite => _stream.CanWrite;

        public override long Length => _stream.Length;

        public override long Position
        {
            get => _stream.Position;
            set => _stream.Position = value;
        }

        public void Rewind(int _)
        {
            _queue.Enqueue(false);
        }
    }
}