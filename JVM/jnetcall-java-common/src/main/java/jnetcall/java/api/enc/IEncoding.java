package jnetcall.java.api.enc;

import jnetbase.java.meta.TypeToken;

public interface IEncoding<TRaw> extends AutoCloseable {

    <T> TRaw encode(T data) throws Exception;

    <T> T decode(TRaw data, Class<T> clazz) throws Exception;
    <T> T decode(TRaw data, TypeToken<T> token) throws Exception;
}
