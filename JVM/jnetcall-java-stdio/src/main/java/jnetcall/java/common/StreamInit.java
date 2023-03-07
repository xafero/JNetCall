package jnetcall.java.common;

import java.io.InputStream;
import java.io.OutputStream;

import jnetcall.java.api.io.ISendTransport;

public interface StreamInit {

    ISendTransport invoke(InputStream stdIn, OutputStream stdOut);
}
