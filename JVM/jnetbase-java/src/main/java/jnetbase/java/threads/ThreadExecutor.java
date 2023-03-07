package jnetbase.java.threads;

import java.util.LinkedList;
import java.util.List;

public final class ThreadExecutor implements IExecutor {

    private final List<Thread> _threads;

    public ThreadExecutor() {
        _threads = new LinkedList<Thread>();
    }

    @Override
    public void close() {
        for (Thread thread : _threads)
            thread.interrupt();
        _threads.clear();
    }

    @Override
    public Thread createThread(Runnable action, String name) {      	
        Thread task = new Thread(action);
        task.setDaemon(true);
        if (name != null)
          	task.setName(name);
        _threads.add(task);
        task.start();
        return task;
    }

    @Override
    public Thread newThread(Runnable action) {
        return createThread(action, null);
    }
}
