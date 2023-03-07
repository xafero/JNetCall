package org.example.api;

import java.util.concurrent.CompletableFuture;

import org.javatuples.Pair;

public interface ISimultaneous extends AutoCloseable {

    CompletableFuture<Integer> getId();

    CompletableFuture<Void> loadIt(String word);

    CompletableFuture<String> removeIt();

    CompletableFuture<Pair<Integer, Long>> runIt(int waitMs, int idx);
}
