package jnetcall.java.api.io;

public interface IPullTransport extends ISendTransport {

    <T> T pull(Class<T> clazz);
}
