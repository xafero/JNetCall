package jnetcall.java.client;

import com.sun.jna.Function;
import jnetbase.java.threads.FuncTimerTask;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;
import jnethotel.java.Clr;
import jnethotel.java.Natives;
import jnethotel.java.api.ICoreClr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class ClrTransport implements IPullTransport {

    private final String _dll;
    private final Timer _timer;
    private final BlockingQueue<Object> _inputs;
    private final BlockingQueue<Object> _outputs;

    public ClrTransport(String dll) {
        try {
            if (!(new File(dll)).exists())
                throw new FileNotFoundException("Missing: " + dll);
            _dll = dll;
            _timer = startTimer(FuncTimerTask.wrap(this::onTick));
            _inputs = new LinkedBlockingQueue<>();
            _outputs = new LinkedBlockingQueue<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onTick(FuncTimerTask t) {
        System.out.println(" " + LocalDateTime.now() + " " + t);

        /*
        synchronized (_sync) {
            _in.addAll(inputs);
            var copy = new ArrayList<MethodResult>();
            _out.drainTo(copy);
            return copy;
        }

          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (var input = new ByteArrayStream();
             var output = new ByteArrayOutputStream();
             var proto = new ProtoConvert(input, output, settings)) {
            var call = pack(method, args);
            if (call == null)
                return null;
            write(proto, call);
            var array = output.toByteArray();
            var result = sendAndGetArray(array);
            input.write(result);
            input.reset();
            var answer = read(MethodResult.class, proto);
            return unpack(method, answer);
        }
         */
    }

    private static Timer startTimer(TimerTask task) {
        var periodMs = 500L;
        var timer = new Timer(true);
        timer.scheduleAtFixedRate(task, periodMs, periodMs);
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
    public void close() {
        _inputs.clear();
        _outputs.clear();
        _timer.cancel();
    }

    private static final Object Sync = new Object();
    private static Clr _vm;
    private static Function _caller;

    private static Clr setupClr(String dll) throws Exception {
        synchronized (Sync) {
            if (_vm != null)
                return _vm;

            var vmRef = Natives.getVmRef();
            vmRef.loadLib();
            var clr = new Clr(vmRef);
            _caller = getCallCallback(clr.getCore(), dll);
            return clr;
        }
    }

    private void prepare() {
        synchronized (Sync) {
            try {
                _vm = setupClr(_dll);
            } catch (Exception e) {
                throw new RuntimeException(_dll, e);
            }
        }
    }

    private static Function getCallCallback(ICoreClr coreClr, String dll)
            throws Exception {
        final var bootType = "X.Boot";
        final var bootMethod = "Call";
        final var bootDelegate = "JNetCall.Sharp.API.CallDelegate, JNetCall.Sharp.InProc";
        return Clr.getCallback(coreClr, dll, bootType, bootMethod, bootDelegate);
    }

    private byte[] sendAndGetArray(byte[] input) {
        synchronized (Sync) {
            var output = _vm.callStaticByteArrayMethod(_caller, input);
            return output;
        }
    }

    private void stop(int milliseconds) {
        var domain = Runtime.getRuntime();
        domain.addShutdownHook(new Thread(() -> {
            try {
                synchronized (Sync) {
                    _vm.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
