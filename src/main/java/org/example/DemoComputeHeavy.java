package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class DemoComputeHeavy {

    public static void main(String[] args) {

        try (var scope = StructuredTaskScope.open()) {
            Subtask<Long> taskA = scope.fork(() -> sumAllIntegers());
            Subtask<String> taskB = scope.fork(() -> getName(1));

            scope.join();

            System.out.println("Integers sum: " + taskA.get());
            System.out.println("Name: " + taskB.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (StructuredTaskScope.FailedException fe) {
            System.out.println("main() A task failed! With cause: " + fe.getCause());
        }
    }

    private static String getName(int userId) {
        try {
            return "Mattias".substring(2000);
        } catch (Exception e) {
            System.out.println("getName() caught exception: " + e);
            throw new RuntimeException(e);
        }
    }

    private static long sumAllIntegers() {
        long total = 0;
        long lastPrintTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.MAX_VALUE - 1; i++) {
            long now = System.currentTimeMillis();
            if (now - lastPrintTime >= 2000) {
                System.out.println("sumAllIntegers() Summed %s integers".formatted(i));
                lastPrintTime = now;
            }
            total++;
        }
        return total;
    }

    private static long sumAllIntegersInterruptAware() throws InterruptedException {
        long total = 0;
        long lastPrintTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.MAX_VALUE - 1; i++) {
            long now = System.currentTimeMillis();
            if (now - lastPrintTime >= 2000) {
                System.out.println("sumAllIntegersInterruptAware() Summed %s integers".formatted(i));
                lastPrintTime = now;
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("sumAllIntegersInterruptAware() I was interrupted, giving up...");
                    throw new InterruptedException();
                }
            }
            total++;
        }
        return total;
    }

}
