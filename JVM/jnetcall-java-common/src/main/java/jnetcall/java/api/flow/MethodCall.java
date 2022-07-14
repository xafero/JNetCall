package jnetcall.java.api.flow;

public record MethodCall(short I, String C, String M, Object[] A)
        implements ICall {
}
