package jnetcall.java.impl.io.disk;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import jnetbase.java.files.FileSystemWatcher;
import jnetbase.java.threads.IExecutor;
import jnetcall.java.api.enc.IEncoding;
import jnetcall.java.api.io.IPushTransport;
import jnetcall.java.api.io.ISendTransport;

public final class FolderTransport implements ISendTransport, IPushTransport, AutoCloseable {

    private final IEncoding<byte[]> _encoding;
    private final Path _inputFolder;
    private final FileSystemWatcher _inputWatch;
    private final Path _outputFolder;
    private final IExecutor _executor;
    private final int _wait;

    public FolderTransport(IEncoding<byte[]> encoding, Path input, Path output, IExecutor executor) {
        _executor = executor;
        _encoding = encoding;
        try {
            _inputFolder = createFolder(input);
            _inputWatch = startWatch();
            _outputFolder = createFolder(output);
            _wait = 5;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String Prefix = "d_";
    private static final String Suffix = ".s";

    private FileSystemWatcher startWatch() {
    	FileSystemWatcher watcher = new FileSystemWatcher(_inputFolder, _executor, ENTRY_CREATE);
        watcher.setFileFilter(FolderTransport::checkName);
        watcher.setCallback(this::onCreated);
        watcher.enableRaisingEvents(true);
        return watcher;
    }

    private static boolean checkName(File f, String s) {
        return s.startsWith(Prefix) && s.endsWith(Suffix);
    }

    private static Path createFolder(Path folder) throws IOException {
        folder = folder.toAbsolutePath();
        return Files.isDirectory(folder) ? folder : Files.createDirectories(folder);
    }

    private Consumer<String> _onPush;

    @Override
    public <T> void onPush(Consumer<T> data, Class<T> clazz) {
        if (data == null) {
            _onPush = null;
            return;
        }
        _onPush = file -> {
            try {
                T msg = get(file, clazz);
                data.accept(msg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void onCreated(Path path) {
        if (_onPush == null)
            return;
        _onPush.accept(path.toString());
    }

    private <T> T get(String sFile, Class<T> clazz) throws Exception {
        String dFile = sFile.substring(0, sFile.length() - Suffix.length());
        byte[] bytes = Files.readAllBytes(Path.of(dFile));
        T msg = _encoding.decode(bytes, clazz);
        CompletableFuture.runAsync(() ->
        {
            try {
                if (_wait >= 1)
                    Thread.sleep(_wait);
                Files.deleteIfExists(Path.of(sFile));
                Files.deleteIfExists(Path.of(dFile));
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        return msg;
    }

    private static final AtomicInteger _fileId = new AtomicInteger();

    private static int getNextId() {
        return _fileId.incrementAndGet();
    }

    @Override
    public <T> void send(T payload) {
        try {
            byte[] bytes = _encoding.encode(payload);
            Path pathData = _outputFolder.resolve(Prefix + getNextId());
            Files.deleteIfExists(pathData);
            Files.write(pathData, bytes);
            Path pathMark = Path.of(pathData + Suffix);
            Files.deleteIfExists(pathMark);
            Files.write(pathMark, new byte[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        _inputWatch.enableRaisingEvents(false);
        _inputWatch.setCallback(null);
        _inputWatch.close();
        _onPush = null;
        _encoding.close();
    }
}
