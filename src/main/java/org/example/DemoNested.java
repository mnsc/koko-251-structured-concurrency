package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.stream.Collectors;

public class DemoNested {
    public static void main(String[] args) {
        try (var scope = StructuredTaskScope.open(Joiner.awaitAllSuccessfulOrThrow(), c -> c.withName("main-scope"))) {

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
        try (var scope2 = StructuredTaskScope.open(
                Joiner.<String>allSuccessfulOrThrow(),
                c -> c.withName("nested-scope"))) {

            "Hello".codePoints().forEach(c -> scope2.fork(() -> WebService.getHomoglyphForCodepoint(c)));

            return scope2.join()
                    .map(st -> st.get())
                    .collect(Collectors.joining());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWorld() {
        return "World";
    }

}
