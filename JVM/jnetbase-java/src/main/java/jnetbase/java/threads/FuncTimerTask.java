package jnetbase.java.threads;

import java.util.TimerTask;
import java.util.function.Consumer;

public final class FuncTimerTask extends TimerTask implements AutoCloseable {

    private Consumer<FuncTimerTask> _worker;

    private FuncTimerTask(Consumer<FuncTimerTask> worker) {
        _worker = worker;
    }

    @Override
    public void run() {
        _worker.accept(this);
    }

    @Override
    public void close() throws Exception {
        cancel();
    }

    public static FuncTimerTask wrap(Consumer<FuncTimerTask> worker) {
        return new FuncTimerTask(worker);
    }
}
