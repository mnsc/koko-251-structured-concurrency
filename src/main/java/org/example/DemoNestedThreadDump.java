package org.example;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

public class DemoNestedThreadDump {
    public static void main(String[] args) {
        ThreadFactory tf = Thread.ofVirtual().name("main-thread-", 100).factory();

        try (var scope = StructuredTaskScope.open(
                Joiner.awaitAllSuccessfulOrThrow(),
                c -> c.withName("main-scope").withThreadFactory(tf))) {

            Subtask<String> taskA = scope.fork(() -> getHello());
            Subtask<String> taskB = scope.fork(() -> getWorld());

            scope.join();

            System.out.println(taskA.get());
            System.out.println(taskB.get());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHello() {

        ThreadFactory tf = Thread.ofVirtual().name("nested-thread-", 1000).factory();

        try (var scope2 = StructuredTaskScope.open(
                Joiner.<String>allSuccessfulOrThrow(),
                c -> c.withName("nested-scope").withThreadFactory(tf))) {

            "Hello".codePoints().forEach(c -> {
                // Create many children
                for (int i = 0; i < 10; i++) {
                scope2.fork(() -> WebService.getHomoglyphForCodepoint(c, Duration.ofSeconds(10)));

                }
            });

            return scope2.join()
                    .map(st -> st.get())
                    .collect(Collectors.joining());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWorld() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(10));
        return "World";
    }

}
