package jnetproto.java.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.javatuples.Tuple;

public interface IDataReader extends AutoCloseable {
    boolean readBool() throws IOException;
    byte readI8() throws IOException;
    short readI16() throws IOException;
    int readI32() throws IOException;
    long readI64() throws IOException;
    float readF32() throws IOException;
    double readF64() throws IOException;
    BigDecimal readF128() throws IOException;
    char readChar() throws IOException;
    String readUtf8() throws IOException;
    Duration readDuration() throws IOException;
    LocalDateTime readTimestamp() throws IOException;
    UUID readGuid() throws IOException;
    Object readArray() throws IOException;
    Map<?,?> readMap() throws IOException;
    Tuple readTuple() throws IOException;
    Set<?> readSet() throws IOException;
    List<?> readList() throws IOException;
    Object[] readBag() throws IOException;
    byte[] readBinary() throws IOException;
    Object readNull();
    Object readObject() throws IOException;
}
