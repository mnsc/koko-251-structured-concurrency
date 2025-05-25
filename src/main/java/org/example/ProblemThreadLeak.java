package org.example;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.example.Logger.log;

public class ProblemThreadLeak {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /*
     * Illustration of a thread leak.
     *
     * Main will first block on futureHello.get()
     *
     * If this fails with an exception, the futureWorld thread will run to completion in the background. A thread leak.
     *
     * Replace ProblemThreadLeak::getHello with ProblemThreadLeak::getHelloThrows to see sad path
     */
    public static void main(String[] args) throws InterruptedException {
        log("Main thread started");

        // (1) submit two tasks
        Future<String> futureHello = executorService.submit(ProblemThreadLeak::getHello);             // <- replace here
        Future<String> futureWorld = executorService.submit(ProblemThreadLeak::getWorld);

        try {
            var helloResult = futureHello.get(); // (2) blocking on result from task getHello
            var worldResult = futureWorld.get();

            log("Main thread result: %s %s".formatted(helloResult, worldResult));
        } catch (ExecutionException e) {
            log("Main thread handles exception: " + e.getCause()); // (3) handle getHello failure
        }
        // (4) getWorld() will still complete a while later

        log("Main thread will now continue on to new things...");

        Thread.sleep(Duration.ofSeconds(120));
    }

    private static String getHello() throws InterruptedException {
        Thread.sleep(500);
        log("getHello() completed");
        return "Hello";
    }

    private static String getHelloThrows() throws InterruptedException {
        Thread.sleep(500);
        log("getHelloThrows() throws Exception");
        return "Hello".substring(100);
    }

    private static String getWorld() throws InterruptedException {
        Thread.sleep(5000);
        log("getWorld() completed");
        return "World";
    }
}
