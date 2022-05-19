package jnetproto.java;

import java.math.BigDecimal;

public interface IDataReader extends AutoCloseable {
    int readI32();
    long readI64();
    float readF32();
    double readF64();
    BigDecimal readF128();
}
