package jnetcall.java.server;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.ICaller;
import jnetcall.java.api.io.ISendTransport;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class ServiceLots {

    public static <T> ClassHosting create(Class<T> serviceClass) {
        var protocol = new NativeHostSink();
        register(protocol);
        return create(serviceClass, protocol);
    }

    private static <T> ClassHosting create(Class<T> serviceClass, ISendTransport protocol) {
        var instance = Reflect.createNew(serviceClass);
        var pool = new ThreadExecutor();
        var host = new ClassHosting(instance, protocol, pool);
        return host;
    }

    private static final List<ICaller> lots = new ArrayList<>();

    private static void register(ICaller lot) {
        lots.add(lot);
    }

    @SuppressWarnings("unused")
    public static byte[] call(byte[] input) throws Exception {
        for (var lot : lots)
            try (var output = new ByteArrayOutputStream()) {
                if (!lot.tryCall(input, output))
                    continue;
                return output.toByteArray();
            }
        return new byte[]{-1};
    }
}