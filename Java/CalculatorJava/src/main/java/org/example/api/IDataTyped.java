package org.example.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDataTyped {

    String ToSimpleText(byte y, short s, int i, long l, float f, double d,
                        boolean b, char c, String t);

    String ToArrayText(byte[] y, short[] s, int[] i, long[] l, float[] f, double[] d,
                       boolean[] b, char[] c, String[] t);

    int GetLineCount(String[] lines);

    long GetFileSize(String path);

    byte[] AllocateBytes(int size, byte value);

    Set<String> GetUnique(List<String> lines, boolean withTrim);

    List<String> GetDouble(Set<String> lines);

    Map<String, Integer> GetSystemVariables(LocalDateTime dts, Duration dur,
                                            Map<String, Integer> parent);
}