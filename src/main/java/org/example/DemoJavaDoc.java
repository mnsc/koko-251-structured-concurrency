package org.example;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class DemoJavaDoc {
    public static MyResult main(String[] args) throws InterruptedException {

        var left = 1;
        var right = 2;

        // copy paste from javadoc

        try (var scope = StructuredTaskScope.open()) {

            Subtask subtask1 = scope.fork(() -> query(left));
            Subtask subtask2 = scope.fork(() -> query(right));

            // throws if either subtask fails
            scope.join();

            // both subtasks completed successfully
            return new MyResult(subtask1.get(), subtask2.get());

        } // close

    }

    private static Object query(int left) {
        return null;
    }

    public record MyResult(Object o, Object o1) {
    }

    public MyResult getResultImperative(int left, int right) {

        var leftQuery = query(left);
        var rightQuery = query(right);

        return new MyResult(leftQuery, rightQuery);
    }

    public MyResult scopeForScreenshot(int left, int right) throws InterruptedException {

        try (var scope = StructuredTaskScope.open()) {

            var leftQuery = scope.fork(() -> query(left));
            var rightQuery = scope.fork(() -> query(right));

            scope.join();

            return new MyResult(leftQuery.get(), rightQuery.get());
        }

    }

    public void fixImperativeScreenshot(){
        // make method name blue :D
        var x = getResultImperative(1, 2);
    }
}
