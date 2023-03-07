package jnetbase.java.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SingleThread<T extends AutoCloseable>
        implements AutoCloseable {

    private final Supplier<T> _creator;
    private final BlockingQueue<Consumer<T>> _actions;
    private final Thread _thread;

    private boolean _running;

    public SingleThread(Supplier<T> creator) {
        _creator = creator;
        _actions = new LinkedBlockingQueue<Consumer<T>>();
        _running = true;
        _thread = new Thread(this::doLoop);
        _thread.setDaemon(true);
        _thread.start();
    }

    @Override
    public void close() throws Exception {
        _running = false;
        _thread.interrupt();
        _actions.clear();
    }

    @SuppressWarnings("removal")
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void execute(Consumer<T> action) {
        _actions.add(action);
    }

    private void doLoop() {
        try {
            doMyLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doMyLoop() throws Exception {
        try (T instance = _creator.get()) {
            while (_running) {
                Consumer<T> action = _actions.take();
                action.accept(instance);
            }
        }
    }
}
