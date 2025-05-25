package org.example;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.example.Logger.log;

public class ProblemCancellationDelay {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /*
     * Illustration of a thread leak.
     *
     * Main will first block on futureHello.get()
     *
     * If futureHello takes a long time and the futureWorld throws to exception fast
     * the futureHello thread will run to completion and then futureHello.get() will throw the exception.
     *
     * The main thread task has waited unnecessarily long to detect failure.
     *
     * Replace ProblemThreadLeak::getWorld with ProblemThreadLeak::getWorldThrows to see sad path
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        log("Main thread started");

        try {
            // (1) submit two tasks
            Future<String> futureHello = executorService.submit(ProblemCancellationDelay::getHello);
            Future<String> futureWorld = executorService.submit(ProblemCancellationDelay::getWorld);         // <- replace here

            var helloResult = futureHello.get(); // (2) blocking (while getWorld fails in background)
            var worldResult = futureWorld.get(); // (3) a while later we query for the result of getWorld
                                                 //     and only now find the Exception

            log("Main thread result: %s %s".formatted(helloResult, worldResult));
        } catch (ExecutionException e) {
            log("Main thread failed: " + e.getCause()); // (4) delayed exception handling
        }

    }

    private static String getHello() throws InterruptedException {
        Thread.sleep(5000);
        log("getHello() completed");
        return "Hello";
    }

    private static String getWorld() throws InterruptedException {
        Thread.sleep(500);
        log("getWorld() completed");
        return "World";
    }

    private static String getWorldThrows() throws InterruptedException {
        Thread.sleep(500);
        log("getHelloThrows() throws Exception");
        return "World".substring(100);
    }
}
