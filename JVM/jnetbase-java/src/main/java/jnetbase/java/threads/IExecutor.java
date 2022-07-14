package jnetbase.java.threads;

import java.util.concurrent.ThreadFactory;

public interface IExecutor extends ThreadFactory, AutoCloseable {

    Thread createThread(Runnable action, String name);
}
