package org.example.api;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import java.util.EnumSet;

public interface IMultiple {

    Pair<Integer, String> GetTuple2T(int a, String b);
    Pair<Integer, String> GetTuple2V(Pair<Integer, String> v);

    Triplet<Integer, String, Boolean> GetTuple3T(int a, String b, boolean c);
    Triplet<Integer, String, Boolean> GetTuple3V(Triplet<Integer, String, Boolean> v);

    Quartet<String, String[], Integer, int[]> GetTuple4T(String a, String[] b, int c, int[] d);
    Quartet<String, String[], Integer, int[]> GetTuple4V(Quartet<String, String[], Integer, int[]> v);

    Quintet<Integer, Float, Long, String, String> GetTuple5T(int a, float b, long c, String d, String e);
    Quintet<Integer, Float, Long, String, String> GetTuple5V(Quintet<Integer, Float, Long, String, String> v);

    WeekDay FindBestDay(int value);
    EnumSet<Days> FindFreeDays();
    String GetTextOf(WeekDay[] taken, EnumSet<Days> days);

    enum WeekDay {
        Monday(1),
        Tuesday(2),
        Wednesday(3),
        Thursday(4),
        Friday(5),
        Saturday(6),
        Sunday(7);

        public final int Value; WeekDay(int n) { Value = n; }
    }

    enum Days {
        None(0),
        Sunday(1),
        Monday(2),
        Tuesday(4),
        Wednesday(8),
        Thursday(16),
        Friday(32),
        Saturday(64);

        public final int Value; Days(int n) { Value = n; }
    }
}