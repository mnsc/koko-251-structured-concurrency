package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;

public class DemoExceptionHandling {
    public static void main(String[] args) throws MyScopeFailedException, InterruptedException {
        try (var scope = StructuredTaskScope.open()) {

            var task1 = scope.fork(()-> throwsSomething());
            var task2 = scope.fork(()-> throwsSomethingElse());

            scope.join();
            System.out.printf("%s %s", task1.get(), task2.get());

        } catch (StructuredTaskScope.FailedException fe) {
            // Cause is the "real" exception that was first thrown
            Throwable cause = fe.getCause();

            switch (cause) {
                case IOException _ ->
                        System.out.println("IOException!");
                case StringIndexOutOfBoundsException _ ->
                        System.out.println("StringIndexOutOfBoundsException!");
                default ->
                        throw new MyScopeFailedException("My scope has failed!", cause);
            }
        }
    }




    private static Object throwsSomethingElse() throws InterruptedException {
        Thread.sleep(new Random().nextInt(1000));
        return "Hello".substring(123);
    }

    private static Object throwsSomething() throws IOException, InterruptedException {
        Thread.sleep(new Random().nextInt(1000));
        return Files.readAllBytes(Path.of("non-existing-file.txt"));
    }

    public static class MyScopeFailedException extends Throwable {
        public MyScopeFailedException(String s, Throwable cause) {
            super(s, cause);
        }
    }
}
