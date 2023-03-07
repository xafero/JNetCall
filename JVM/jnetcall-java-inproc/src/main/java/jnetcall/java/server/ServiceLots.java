package jnetcall.java.server;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jnetbase.java.meta.Reflect;
import jnetbase.java.threads.ThreadExecutor;
import jnetcall.java.api.ICaller;
import jnetcall.java.api.io.ISendTransport;

public final class ServiceLots {

    public static <T> ClassHosting create(Class<T> serviceClass) {
    	ServiceLot protocol = new ServiceLot();
        register(protocol);
        return create(serviceClass, protocol);
    }

    private static <T> ClassHosting create(Class<T> serviceClass, ISendTransport protocol) {
        T instance = Reflect.createNew(serviceClass);
        ThreadExecutor pool = new ThreadExecutor();
        ClassHosting host = new ClassHosting(instance, protocol, pool);
        return host;
    }

    private static final List<ICaller> lots = new ArrayList<>();

    private static void register(ICaller lot) {
        lots.add(lot);
    }

    @SuppressWarnings("unused")
    public static byte[] call(byte[] input) throws Exception {
        for (ICaller lot : lots)
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                if (!lot.tryCall(input, output))
                    continue;
                return output.toByteArray();
            }
        return new byte[]{-1};
    }
}