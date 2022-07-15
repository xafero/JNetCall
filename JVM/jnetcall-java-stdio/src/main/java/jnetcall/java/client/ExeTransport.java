package jnetcall.java.client;

import jnetbase.java.sys.Strings;
import jnetcall.java.api.io.IPullTransport;
import jnetcall.java.api.io.ISendTransport;
import jnetcall.java.client.tools.ServiceEnv;
import jnetproto.java.tools.Tuples;
import org.javatuples.Pair;

import java.io.*;
import java.util.concurrent.TimeUnit;

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
            var std = start();
            _parent = (IPullTransport) init.invoke(std.getValue0(), std.getValue1());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Process _process;

    private Pair<InputStream, OutputStream> start() throws IOException {
        var pwd = ServiceEnv.getCurrentDir();
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

    private static Pair<InputStream, OutputStream> writeSync(Process process) throws IOException {
        var stdOut = process.getInputStream();
        var stdIn = process.getOutputStream();
        final int marker = 0xEE;
        // Send flag
        stdIn.write(marker);
        stdIn.flush();
        // Receive flag
        while (stdOut.read() != marker) ;
        // Ready!
        return Tuples.create(stdOut, stdIn);
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
    public void close() throws Exception {
        stop(250);
        _parent.close();
    }

    @Override
    public <T> T pull(Class<T> clazz) {
        var msg = _parent.pull(clazz);
        return msg;
    }
}
