package jnetcall.java.server.api;

public interface IHosting extends AutoCloseable {

    void addServiceEndpoint(Class<?> type);

    <T> T goDynInvoke(Class<T> type, short id, Object... args);
}
