package jnetbase.java;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public final class Tasks {

    private static final Object lock = new Object();

    private static ExecutorService fixedPool;

    public static ExecutorService getPool() {
        synchronized (lock) {
            if (fixedPool != null && !fixedPool.isShutdown())
                return fixedPool;
            var runtime = Runtime.getRuntime();
            var processors = runtime.availableProcessors();
            return fixedPool = Executors.newFixedThreadPool(processors);
        }
    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<Future<T>> invokeAll(List<Callable<T>> tasks)
            throws InterruptedException {
        var executor = Tasks.getPool();
        return executor.invokeAll(tasks);
    }

    public static <T> List<T> whenAllInvoke(List<Callable<T>> tasks)
            throws InterruptedException {
        var future = invokeAll(tasks);
        var unpack = future.stream().map(f -> Tasks.get(f));
        return unpack.collect(Collectors.toList());
    }

    public static <T> List<T> whenAll(Collection<CompletableFuture<T>> tasks) {
        var array = tasks.toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(array).join();
        return tasks.stream().map(t -> get(t)).toList();
    }

    private static <T> T get(Future<T> task) {
        try {
            return task.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
