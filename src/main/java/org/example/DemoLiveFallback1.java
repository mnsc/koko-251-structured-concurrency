package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

/*
 TODO
  1) basic example hello world!
  2) cancellation
  3) nested scope with "all successful" policy
* */
public class DemoLiveFallback1 {
    public static void main(String[] args) {
        try (var scope = StructuredTaskScope.open()) {

            var task = scope.fork(() -> "Hello");
            var task2 = scope.fork(() -> "World");

            scope.join();

            System.out.println(task.get());
            System.out.println(task2.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
