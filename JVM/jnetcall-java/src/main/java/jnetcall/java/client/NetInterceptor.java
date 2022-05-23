package jnetcall.java.client;

import jnetcall.java.api.MethodCall;
import jnetcall.java.api.MethodResult;
import jnetcall.java.api.MethodStatus;
import jnetcall.java.tools.Conversions;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;
import jnetproto.java.compat.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.ProcessBuilder.Redirect;

public final class NetInterceptor implements InvocationHandler, AutoCloseable {
    private static final ProtoSettings settings = new ProtoSettings();

    private final String _exe;

    public NetInterceptor(String exe) {
        try {
            _exe = exe;
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Process _process;
    private ProtoConvert _convert;

    private void start() throws IOException {
        if (!(new File(_exe)).exists()) {
            throw new FileNotFoundException("Missing: " + _exe);
        }
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

    private void stop() throws InterruptedException {
        stop(250);
    }

    private void stop(int milliseconds) throws InterruptedException {
        _process.waitFor(milliseconds, TimeUnit.MILLISECONDS);
        _process.destroyForcibly();
    }

    private void write(Object obj) throws Exception {
        _convert.writeObject(obj);
        _convert.flush();
    }

    private <T> T read(Class<T> clazz) throws IOException {
        try {
            var obj = _convert.readObject(clazz);
            return obj;
        } catch (Exception e) {
            _process.getOutputStream().close();
            var error = (Strings.readToEnd(_process.getInputStream()) + " " +
                    Strings.readToEnd(_process.getErrorStream())).trim();
            throw new UnsupportedOperationException(error, e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var call = new MethodCall(method.getDeclaringClass().getSimpleName(),
                method.getName(), args);
        if (call.C().equals("AutoCloseable") && call.M().equals("close")) {
            close();
            return null;
        }
        write(call);
        var input = read(MethodResult.class);
        var status = Arrays.stream(MethodStatus.values())
                .filter(m -> m.getValue() == input.S())
                .findFirst().orElse(MethodStatus.Unknown);
        switch (status) {
            case Ok:
                var raw = Conversions.convert(method.getReturnType(), input.R());
                return raw;
            default:
                throw new UnsupportedOperationException("[" + input.S() + "] " + input.R());
        }
    }

    @Override
    public void close() throws Exception {
        stop();
        _process.destroy();
    }
}
