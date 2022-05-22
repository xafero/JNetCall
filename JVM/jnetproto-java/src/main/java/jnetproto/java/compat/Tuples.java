package jnetproto.java.compat;

import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Septet;
import org.javatuples.Sextet;
import org.javatuples.Triplet;
import org.javatuples.Unit;

public final class Tuples {

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Octet<T1, T2, T3, T4, T5, T6, T7, T8> create(
            T1 a, T2 b, T3 c, T4 d, T5 e, T6 f, T7 g, T8 h) {
        return Octet.with(a, b, c, d, e, f, g, h);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Septet<T1, T2, T3, T4, T5, T6, T7> create(
            T1 a, T2 b, T3 c, T4 d, T5 e, T6 f, T7 g) {
        return Septet.with(a, b, c, d, e, f, g);
    }

    public static <T1, T2, T3, T4, T5, T6> Sextet<T1, T2, T3, T4, T5, T6> create(
            T1 a, T2 b, T3 c, T4 d, T5 e, T6 f) {
        return Sextet.with(a, b, c, d, e, f);
    }

    public static <T1, T2, T3, T4, T5> Quintet<T1, T2, T3, T4, T5> create(T1 a, T2 b, T3 c, T4 d, T5 e) {
        return Quintet.with(a, b, c, d, e);
    }

    public static <T1, T2, T3, T4> Quartet<T1, T2, T3, T4> create(T1 a, T2 b, T3 c, T4 d) {
        return Quartet.with(a, b, c, d);
    }

    public static <T1, T2, T3> Triplet<T1, T2, T3> create(T1 a, T2 b, T3 c) {
        return Triplet.with(a, b, c);
    }

    public static <T1, T2> Pair<T1, T2> create(T1 a, T2 b) {
        return Pair.with(a, b);
    }

    public static <T1> Unit<T1> create(T1 a) {
        return Unit.with(a);
    }
}
