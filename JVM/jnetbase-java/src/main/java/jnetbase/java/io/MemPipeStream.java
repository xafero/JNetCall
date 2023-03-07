package jnetbase.java.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class MemPipeStream implements Flushable, Closeable {

    private final byte[] _array;
    private final Input _input;
    private final Output _output;
    private final BlockingQueue<Boolean> _queue;

    public MemPipeStream() {
        _array = new byte[0];
        _input = new Input(_array);
        _output = new Output();
        _queue = new LinkedBlockingQueue<Boolean>();
    }

    private int _readPos;
    private int _writePos;

    public InputStream asI() {
        return _input;
    }

    public OutputStream asO() {
        return _output;
    }

    @Override
    public void flush() throws IOException {
        _output.flush();
    }

    @Override
    public void close() throws IOException {
        _queue.clear();
        _output.close();
        _input.close();
    }

    private final class Input extends ByteArrayInputStream {

        public Input(byte[] buf) {
            super(buf);
        }

        @Override
        public int read(byte[] b) {
            try {
                _queue.take();
                pos = _readPos;
                int res = super.read(b, 0, b.length);
                if (res >= 0)
                    _readPos += res;
                return res;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) {
            pos = _readPos;
            int res = super.read(b, off, len);
            if (res >= 0)
                _readPos += res;
            return res;
        }

        synchronized void replace(byte[] array) {
            buf = array;
            count = array.length;
        }
    }

    private final class Output extends ByteArrayOutputStream implements IRewindable {

        public Output() {
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            count = _writePos;
            super.write(b, off, len);
            _writePos += len;
        }

        @Override
        public void rewind(int size) {
            _input.replace(_output.toByteArray());
            _queue.offer(true);
        }
    }
}
