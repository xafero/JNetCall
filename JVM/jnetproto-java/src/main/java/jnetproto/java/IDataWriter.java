package jnetproto.java;

import org.javatuples.Tuple;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface IDataWriter extends AutoCloseable {
    void writeBool(boolean value) throws IOException;
    void writeI8(byte value) throws IOException;
    void writeI16(short value) throws IOException;
    void writeI32(int value) throws IOException;
    void writeI64(long value) throws IOException;
    void writeF32(float value) throws IOException;
    void writeF64(double value) throws IOException;
    void writeF128(BigDecimal value) throws IOException;
    void writeChar(char value) throws IOException;
    void writeUtf8(String value) throws IOException;
    void writeDuration(Duration value) throws IOException;
    void writeTimestamp(LocalDateTime value) throws IOException;
    void writeGuid(UUID value) throws IOException;
    void writeArray(Object value) throws IOException;
    void writeMap(Map value) throws IOException;
    void writeTuple(Tuple value) throws IOException;
    void writeObject(Object value) throws IOException;
}
