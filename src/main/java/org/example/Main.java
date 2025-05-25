package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class Main {
    public static void main(String[] args) {

        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            Subtask<Integer> one = scope.fork(()-> 1);
            scope.join();
            System.out.println(one.get());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}