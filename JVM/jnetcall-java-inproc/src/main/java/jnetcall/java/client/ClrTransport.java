package jnetcall.java.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jnetbase.java.meta.TypeToken;
import jnetbase.java.threads.FuncTimerTask;
import jnetbase.java.threads.ManualResetEvent;
import jnetbase.java.threads.SingleThread;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.impl.enc.BinaryEncoding;

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

    private static final TypeToken<List<MethodResult>> mrList = new TypeToken<List<MethodResult>>() {
    };

    private void onTick(FuncTimerTask t) {
        try {
            synchronized (_sync) {
                ArrayList<MethodCall> outputs = new ArrayList<MethodCall>();
                ((BlockingQueue) _outputs).drainTo(outputs);
                byte[] output = _encoding.encode(outputs);
                byte[] input = sendAndGetArray(output);
                List<MethodResult> inputs = _encoding.decode(input, mrList);
                _inputs.addAll(inputs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] sendAndGetArray(byte[] input) throws InterruptedException {
        synchronized (_sync) {
            byte[][] res = new byte[1][];
            ManualResetEvent wait = new ManualResetEvent(false);
            _single.execute(i ->
            {
                res[0] = i.sendAndGetArray(input);
                wait.set();
            });
            wait.waitOne();
            byte[] bytes = res[0];
            return bytes;
        }
    }

    private static Timer startTimer(TimerTask task, long pollMs) {
        Timer timer = new Timer(true);
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
