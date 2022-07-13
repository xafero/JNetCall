package jnetbase.java.sys;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public final class BitConverter {

    private static ByteBuffer allocate(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
    }

    private static ByteBuffer wrap(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
    }

    public static byte[] getBytes(short value) {
        return allocate(2).putShort(value).array();
    }

    public static short toInt16(byte[] bytes) {
        return wrap(bytes).getShort();
    }

    public static byte[] getBytes(int value) {
        return allocate(4).putInt(value).array();
    }

    public static int toInt32(byte[] bytes) {
        return wrap(bytes).getInt();
    }

    public static byte[] getBytes(long value) {
        return allocate(8).putLong(value).array();
    }

    public static long toInt64(byte[] bytes) {
        return wrap(bytes).getLong();
    }

    public static byte[] getBytes(float value) {
        return allocate(4).putFloat(value).array();
    }

    public static float toSingle(byte[] bytes) {
        return wrap(bytes).getFloat();
    }

    public static byte[] getBytes(double value) {
        return allocate(8).putDouble(value).array();
    }

    public static double toDouble(byte[] bytes) {
        return wrap(bytes).getDouble();
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

    public static UUID toGuid(byte[] bytes) {
        var raw = wrap(bytes);
        var first = getBytes(raw.getInt());
        var second = getBytes(raw.getInt());
        var most = toInt64(new byte[]{second[2], second[3], second[0], second[1],
                first[0], first[1], first[2], first[3]});
        var least = raw.order(ByteOrder.BIG_ENDIAN).getLong();
        return new UUID(most, least);
    }
}
