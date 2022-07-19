package jnetcall.java.client;

import jnetbase.java.meta.TypeToken;
import jnetbase.java.threads.FuncTimerTask;
import jnetbase.java.threads.ManualResetEvent;
import jnetbase.java.threads.SingleThread;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.impl.enc.BinaryEncoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class ClrTransport implements IPullTransport {

    private static SingleThread<ClrContainer> _single;

    private final Object _sync;
    private final IEncoding<byte[]> _encoding;
    private final Timer _timer;
    private final BlockingQueue<Object> _inputs;
    private final BlockingQueue<Object> _outputs;

    public ClrTransport(String dll, long pollMs) {
        try {
            if (!(new File(dll)).exists())
                throw new FileNotFoundException("Missing: " + dll);
            _sync = new Object();
            _encoding = new BinaryEncoding();
            _timer = startTimer(FuncTimerTask.wrap(this::onTick), pollMs);
            _inputs = new LinkedBlockingQueue<>();
            _outputs = new LinkedBlockingQueue<>();
            if (_single != null)
                return;
            _single = new SingleThread<ClrContainer>(() -> new ClrContainer(dll));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final TypeToken<List<MethodResult>> mrList = new TypeToken<>() {
    };

    private void onTick(FuncTimerTask t) {
        try {
            synchronized (_sync) {
                var outputs = new ArrayList<MethodCall>();
                ((BlockingQueue) _outputs).drainTo(outputs);
                var output = _encoding.encode(outputs);
                var input = sendAndGetArray(output);
                var inputs = _encoding.decode(input, mrList);
                _inputs.addAll(inputs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] sendAndGetArray(byte[] input) throws InterruptedException {
        synchronized (_sync) {
            var res = new byte[1][];
            var wait = new ManualResetEvent(false);
            _single.execute(i ->
            {
                res[0] = i.sendAndGetArray(input);
                wait.set();
            });
            wait.waitOne();
            var bytes = res[0];
            return bytes;
        }
    }

    private static Timer startTimer(TimerTask task, long pollMs) {
        var timer = new Timer(true);
        timer.scheduleAtFixedRate(task, pollMs, pollMs);
        return timer;
    }

    @Override
    public <T> T pull(Class<T> clazz) {
        try {
            return (T) _inputs.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void send(T payload) {
        _outputs.add(payload);
    }

    @Override
    public void close() throws Exception {
        _inputs.clear();
        _outputs.clear();
        _timer.cancel();
        _encoding.close();
    }
}
