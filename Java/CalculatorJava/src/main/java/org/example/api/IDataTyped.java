package org.example.api;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IDataTyped {

    String ToSimpleText(byte y, short s, int i, long l, float f, double d,
                        boolean b, char c, String t, BigDecimal u, UUID g);

    String ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f, double[] d,
                       boolean[] b, char[] c, String[] t, BigDecimal[] u, UUID[] g);

    int GetLineCount(String[] lines);

    long GetFileSize(String path);

    byte[] AllocateBytes(int size, byte value);

    Set<String> GetUnique(List<String> lines, boolean withTrim);

    List<String> GetDouble(Set<String> lines);

    Map<String, Integer> GetSystemVariables(ZonedDateTime dts, Duration dur,
                                            Map<String, Integer> parent);
}