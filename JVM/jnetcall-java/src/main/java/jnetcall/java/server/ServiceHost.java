package jnetcall.java.server;

import jnetcall.java.api.MethodCall;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ServiceHost<T> extends AbstractHost<T> implements AutoCloseable {

    public ServiceHost(Class<T> serviceClass) {
        super(serviceClass);
    }

    private static ProtoConvert readSync(InputStream in, OutputStream out, ProtoSettings cfg)
            throws IOException {
        var convert = new ProtoConvert(in, out, cfg);
        final int marker = 0xEE;
        // Send flag
        out.write(marker);
        out.flush();
        // Receive flag
        while (in.read() != marker) ;
        // Ready!
        return convert;
    }

    public void open(InputStream input, OutputStream output) throws Exception {
        var inst = createInst();
        var methods = inst.getClass().getMethods();
        try (var proto = readSync(input, output, config)) {
            MethodCall call;
            while ((call = proto.readObject(MethodCall.class)) != null) {
                handleCall(inst, methods, call, proto);
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}