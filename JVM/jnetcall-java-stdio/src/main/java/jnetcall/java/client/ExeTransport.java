package jnetcall.java.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.javatuples.Pair;

import jnetbase.java.sys.Strings;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.tools.ServiceEnv;
import jnetcall.java.common.ByteMarks;
import jnetcall.java.common.StreamInit;

final class ExeTransport implements ISendTransport, IPullTransport {

    private final String _exe;
    private final IPullTransport _parent;

    public ExeTransport(String exe, StreamInit init) {
        try {
            if (!(new File(exe)).exists() && exe.endsWith(".exe"))
                exe = exe.substring(0, exe.length() - 4);
            if (!(new File(exe)).exists())
                throw new FileNotFoundException("Missing: " + exe);
            _exe = exe;
            Pair<InputStream, OutputStream> std = start();
            _parent = (IPullTransport) init.invoke(std.getValue0(), std.getValue1());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Process _process;

    private Pair<InputStream, OutputStream> start() throws IOException {
        File pwd = ServiceEnv.getCurrentDir();
        _process = new ProcessBuilder(_exe)
                .directory(pwd)
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start();
        return writeSync(_process);
    }

    private void stop(int milliseconds) throws InterruptedException {
        _process.waitFor(milliseconds, TimeUnit.MILLISECONDS);
        _process.destroyForcibly();

        _process.destroy();
    }

    private static Pair<InputStream, OutputStream> writeSync(Process process)
            throws IOException {
        InputStream stdOut = process.getInputStream();
        OutputStream stdIn = process.getOutputStream();
        return ByteMarks.writeSync(stdOut, stdIn);
    }

    private String getErrorDetails() throws IOException {
        _process.getOutputStream().close();
        return (Strings.readToEnd(_process.getInputStream()) + " " +
                Strings.readToEnd(_process.getErrorStream())).trim();
    }

    @Override
    public <T> void send(T payload) {
        _parent.send(payload);
    }

    @Override
    public <T> T pull(Class<T> clazz) {
    	T msg = _parent.pull(clazz);
        return msg;
    }

    @Override
    public void close() throws Exception {
        stop(250);
        _parent.close();
    }
}
