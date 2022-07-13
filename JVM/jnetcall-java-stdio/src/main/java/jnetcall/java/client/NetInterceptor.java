package jnetcall.java.client;

import jnetbase.java.sys.Strings;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static java.lang.ProcessBuilder.Redirect;

public final class NetInterceptor extends AbstractInterceptor {

    public NetInterceptor(String exe) {
        super(exe);
    }

    private Process _process;
    private ProtoConvert _convert;

    @Override
    protected void prepare() {
    }

    @Override
    protected void start() throws IOException {
        var pwd = ServiceEnv.getCurrentDir();
        _process = new ProcessBuilder(_exe)
                .directory(pwd)
                .redirectInput(Redirect.PIPE)
                .redirectError(Redirect.PIPE)
                .redirectOutput(Redirect.PIPE)
                .start();
        _convert = writeSync(_process, settings);
    }

    private static ProtoConvert writeSync(Process process, ProtoSettings cfg) throws IOException {
        var stdOut = process.getInputStream();
        var stdIn = process.getOutputStream();
        var convert = new ProtoConvert(stdOut, stdIn, cfg);
        final int marker = 0xEE;
        // Send flag
        stdIn.write(marker);
        stdIn.flush();
        // Receive flag
        while (stdOut.read() != marker) ;
        // Ready!
        return convert;
    }

    @Override
    protected void stop(int milliseconds) throws InterruptedException {
        _process.waitFor(milliseconds, TimeUnit.MILLISECONDS);
        _process.destroyForcibly();

        _process.destroy();
    }

    @Override
    protected String getErrorDetails() throws IOException {
        _process.getOutputStream().close();
        return (Strings.readToEnd(_process.getInputStream()) + " " +
                Strings.readToEnd(_process.getErrorStream())).trim();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokeBase(method, args, _convert);
    }
}
