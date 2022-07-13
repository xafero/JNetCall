package jnetbase.java.files;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class FileSystemWatcher implements AutoCloseable {

    private final Path _folder;
    private final WatchService _watcher;
    private final List<WatchEvent.Kind<?>> _kinds;
    private final ThreadFactory _executor;

    public FileSystemWatcher(Path folder, ThreadFactory executor, WatchEvent.Kind... kinds) {
        var root = folder.toFile();
        if (!root.exists() || root.isFile())
            throw new UnsupportedOperationException(root.getAbsolutePath());
        try {
            _folder = folder;
            _watcher = FileSystems.getDefault().newWatchService();
            _kinds = Arrays.asList(kinds);
            _executor = executor;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableRaisingEvents(boolean value) {
        try {
            if (value)
                start();
            else
                stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WatchKey _key;
    private Thread _thread;
    private FilenameFilter _filter;
    private Consumer<Path> _callback;

    private void stop() {
        if (_key != null)
            _key.cancel();
        _key = null;
        if (_thread != null)
            _thread.interrupt();
        _thread = null;
    }

    public void setFileFilter(FilenameFilter filter) {
        _filter = filter;
    }

    public void setCallback(Consumer<Path> handler) {
        _callback = handler;
    }

    private void runLoop() throws InterruptedException {
        while (!Thread.interrupted()) {
            var key = _watcher.take();
            var events = key.pollEvents();
            key.reset();
            for (var event : events) {
                var kind = event.kind();
                if (kind == OVERFLOW || !_kinds.contains(kind))
                    continue;
                var ctx = event.context();
                if (!_filter.accept(null, ctx.toString()))
                    continue;
                var path = (Path) ctx;
                var full = _folder.resolve(path);
                sendNotification(full);
            }
        }
    }

    private void sendNotification(Path path) {
        _callback.accept(path);
    }

    private void tryRun() {
        try {
            runLoop();
        } catch (ClosedWatchServiceException | InterruptedException e) {
            // Just ignore
        }
    }

    private void start() throws IOException {
        var args = _kinds.toArray(WatchEvent.Kind[]::new);
        _key = _folder.register(_watcher, args);
        _thread = _executor.newThread(this::tryRun);
        _thread.setName("FileSystemWatcher");
        _thread.setDaemon(true);
        _thread.start();
    }

    @Override
    public void close() throws Exception {
        stop();
        _watcher.close();
    }
}
