package jnetcall.java.api;

import java.io.OutputStream;

public interface ICaller {

    boolean tryCall(byte[] in, OutputStream output) throws Exception;
}
