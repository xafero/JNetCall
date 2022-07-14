package jnetcall.java.api.enc;

public interface IEncoding<TRaw> extends AutoCloseable {

    <T> TRaw encode(T data) throws Exception;

    <T> T decode(TRaw data, Class<T> clazz) throws Exception;
}
