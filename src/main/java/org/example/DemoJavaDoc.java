package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class DemoJavaDoc {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(javadocCode(1, 2));
    }

    private static MyResult javadocCode(int left, int right) throws InterruptedException {

        try (var scope = StructuredTaskScope.open()) {

            Subtask subtask1 = scope.fork(() -> query(left));
            Subtask subtask2 = scope.fork(() -> query(right));

            // throws if either subtask fails
            scope.join();

            // both subtasks completed successfully
            return new MyResult(subtask1.get(), subtask2.get());

        } // close

    }

    private static Object query(int param) {
        return param * 2;
    }

    public record MyResult(Object left, Object right) {
    }
}
