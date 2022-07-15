package jnetcall.java.client;

import jnetcall.java.api.io.ISendTransport;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamInit {

    ISendTransport invoke(InputStream stdIn, OutputStream stdOut);
}
