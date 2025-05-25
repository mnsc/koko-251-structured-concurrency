package org.example;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class DemoJoinerAnySuccessful {

    public static void main(String[] args) throws InterruptedException {
        int userId = 1; // TODO: why is higher userid slower from cache??
        Combo combo1 = getDefaultJoinerPolicy(userId);
        System.out.println("Rating default: " + combo1);

        Combo combo2 = getAnySuccessfulPolicyWithTimeout(userId, "Mattias");
        System.out.println("Rating with cache: " + combo2);
    }

    private static Combo getDefaultJoinerPolicy(int userId) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
                // this is open()
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow()
        )) {
            Subtask<Double> rating = scope.fork(() -> WebService.getRatingForUser(userId));
            Subtask<String> userName = scope.fork(() -> getName(userId));
            scope.join();
            return new Combo(rating.get(), userName.get());
        }
    }


    private static Combo getAnySuccessfulPolicyWithTimeout(int userId, String userName) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<Double>anySuccessfulResultOrThrow(),
                c -> c.withTimeout(Duration.ofMillis(500))
        )) {
            scope.fork(() -> getRatingFromCache1(userId));
            scope.fork(() -> getRatingFromCache2(userId));
            scope.fork(() -> getRatingFromCache3(userId));
            var rating = scope.join();
            System.out.println("Found rating in cache!");
            return new Combo(rating, userName);
        } catch (StructuredTaskScope.TimeoutException te) {
            System.out.println("Cache did not respond quickly enough, going to service...");
            var rating = WebService.getRatingForUser(userId);
            return new Combo(rating, userName);
        }
    }


    private static String getName(int userId) {
        return "Mattias #%s".formatted(userId);
    }

    private static double getRatingFromCache1(int userId) throws InterruptedException {
        Thread.sleep(42L * userId);
        return 4.5;
    }

    private static double getRatingFromCache2(int userId) throws InterruptedException {
        Thread.sleep(15L * userId);
        return 4.5;
    }

    private static double getRatingFromCache3(int userId) throws InterruptedException {
        Thread.sleep(70L * userId);
        return 4.5;
    }

    private record Combo(Double rating, String name) {
    }
}
