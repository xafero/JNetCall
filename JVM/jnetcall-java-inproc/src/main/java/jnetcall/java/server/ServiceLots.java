package jnetcall.java.server;

import jnetcall.java.api.ICaller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class ServiceLots {

    public static <T> ServiceLot<T> create(Class<T> serviceClass) {
        var instance = new ServiceLot<>(serviceClass);
        return instance;
    }

    private static final List<ICaller> lots = new ArrayList<>();

    static <T> void register(ServiceLot<T> lot) {
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