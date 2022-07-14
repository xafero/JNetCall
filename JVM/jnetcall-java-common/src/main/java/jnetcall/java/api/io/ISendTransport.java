package jnetcall.java.api.io;

public interface ISendTransport extends AutoCloseable {

    <T> void send(T payload);
}
