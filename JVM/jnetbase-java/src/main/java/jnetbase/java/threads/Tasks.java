package jnetbase.java.threads;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jnetbase.java.compat.J8;

public final class Tasks {

    private static final Object lock = new Object();

    private static ExecutorService fixedPool;

    public static ExecutorService getPool() {
        synchronized (lock) {
            if (fixedPool != null && !fixedPool.isShutdown())
                return fixedPool;
            Runtime runtime = Runtime.getRuntime();
            int processors = runtime.availableProcessors();
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
        ExecutorService executor = Tasks.getPool();
        return executor.invokeAll(tasks);
    }

    public static <T> List<T> whenAllInvoke(List<Callable<T>> tasks)
            throws InterruptedException {
        List<Future<T>> future = invokeAll(tasks);
        Stream<T> unpack = future.stream().map(f -> Tasks.get(f));
        return unpack.collect(Collectors.toList());
    }

    public static <T> List<T> whenAll(Collection<CompletableFuture<T>> tasks) {
    	CompletableFuture[] rawArray = new CompletableFuture[tasks.size()];
        CompletableFuture[] array = tasks.toArray(rawArray);
        CompletableFuture.allOf(array).join();
        return J8.toList(tasks.stream().map(t -> get(t)));
    }

    private static <T> T get(Future<T> task) {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T await(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> CompletableFuture<T> run(Callable<Callable<T>> callable) {
        return CompletableFuture.supplyAsync(() -> await(await(callable)));
    }

    public static <T> T await(CompletableFuture<T> callable) {
        return Tasks.blockGet(callable);
    }

    private static <T> T blockGet(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> CompletableFuture<T> wrapBlock(Future<T> future) {
        try {
            return CompletableFuture.completedFuture(future.get());
        } catch (InterruptedException e) {
            return J8.failedFuture(e);
        } catch (ExecutionException e) {
            return J8.failedFuture(e.getCause());
        }
    }

    public static <T> CompletableFuture<T> wrap(Future<T> future) {
        return future instanceof CompletableFuture ? (CompletableFuture<T>)future : wrap(future::get);
    }

    public static <T> CompletableFuture<T> wrap(Callable<T> callable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
