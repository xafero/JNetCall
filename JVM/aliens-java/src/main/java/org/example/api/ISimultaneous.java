package org.example.api;

import java.util.concurrent.CompletionStage;

public interface ISimultaneous extends AutoCloseable {

    CompletionStage<Integer> getId();

    CompletionStage<Void> loadIt(String word);

    CompletionStage<String> removeIt();
}
