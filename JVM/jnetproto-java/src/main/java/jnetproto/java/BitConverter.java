package jnetproto.java;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public final class BitConverter {

    private static ByteBuffer allocate(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
    }

    public static byte[] getBytes(short value) {
        return allocate(2).putShort(value).array();
    }

    public static byte[] getBytes(int value) {
        return allocate(4).putInt(value).array();
    }

    public static byte[] getBytes(long value) {
        return allocate(8).putLong(value).array();
    }

    public static byte[] getBytes(float value) {
        return allocate(4).putFloat(value).array();
    }

    public static byte[] getBytes(double value) {
        return allocate(8).putDouble(value).array();
    }

    public static byte[] getBytes(UUID value) {
        var most = getBytes(value.getMostSignificantBits());
        return allocate(16)
                .put(most, 4, 4)
                .put(most, 2, 1)
                .put(most, 3, 1)
                .put(most, 0, 1)
                .put(most, 1, 1)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(value.getLeastSignificantBits())
                .array();
    }
}
