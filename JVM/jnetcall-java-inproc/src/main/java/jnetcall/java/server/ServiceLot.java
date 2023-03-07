package jnetcall.java.server;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jnetbase.java.meta.TypeToken;
import jnetcall.java.api.ICaller;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.flow.MethodCall;
import jnetcall.java.api.flow.MethodResult;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.impl.enc.BinaryEncoding;

public final class ServiceLot implements ICaller, IPullTransport {

    private final Object _sync;
    private final IEncoding<byte[]> _encoding;
    private final BlockingQueue<Object> _inputs;
    private final BlockingQueue<Object> _outputs;

    public ServiceLot() {
        _sync = new Object();
        _encoding = new BinaryEncoding();
        _inputs = new LinkedBlockingQueue<>();
        _outputs = new LinkedBlockingQueue<>();
    }

    @Override
    public <T> T pull(Class<T> clazz) {
        try {
            return clazz.cast(_inputs.take());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void send(T payload) {
        _outputs.add(payload);
    }

    private List<MethodResult> synchronize(Collection<MethodCall> inputs) {
        synchronized (_sync) {
            _inputs.addAll(inputs);
            ArrayList<MethodResult> copy = new ArrayList<MethodResult>();
            ((BlockingQueue) _outputs).drainTo(copy);
            return copy;
        }
    }

    private static final TypeToken<List<MethodCall>> mcList = new TypeToken() {
    };

    @Override
    public boolean tryCall(byte[] in, OutputStream output) throws Exception {
        List<MethodCall> calls = _encoding.decode(in, mcList);
        List<MethodResult> answers = synchronize(calls);
        byte[] bytes = _encoding.encode(answers);
        output.write(bytes);
        output.flush();
        return true;
    }

    @Override
    public void close() throws Exception {
        _inputs.clear();
        _outputs.clear();
        _encoding.close();
    }
}
