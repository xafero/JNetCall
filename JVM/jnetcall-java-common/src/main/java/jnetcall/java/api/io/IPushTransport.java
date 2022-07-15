package jnetcall.java.api.io;

import java.util.function.Consumer;

public interface IPushTransport extends ISendTransport {

    <T> void onPush(Consumer<T> data, Class<T> clazz);
}
