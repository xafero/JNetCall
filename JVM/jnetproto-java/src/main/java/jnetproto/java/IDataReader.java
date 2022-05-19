package jnetproto.java;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IDataReader extends AutoCloseable {
    byte readU8() throws IOException;
    byte readI8() throws IOException;
    short readI16() throws IOException;
    int readI32() throws IOException;
    long readI64() throws IOException;
    float readF32() throws IOException;
    double readF64() throws IOException;
    BigDecimal readF128() throws IOException;
    String readUtf8() throws IOException;
    Duration readDuration() throws IOException;
    LocalDateTime readTimestamp() throws IOException;
    UUID readGuid() throws IOException;
    Object readObject() throws IOException;
}