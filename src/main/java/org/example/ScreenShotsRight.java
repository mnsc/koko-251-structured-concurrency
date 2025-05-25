package org.example;

import java.util.concurrent.StructuredTaskScope;

public class ScreenShotsRight {

    // Screenshot to the right on slide
    public MyResult scopeForScreenshot(int left, int right) throws InterruptedException {

        try (var scope = StructuredTaskScope.open()) {

            var leftQuery = scope.fork(() -> query(left));
            var rightQuery = scope.fork(() -> query(right));

            scope.join();

            return new MyResult(leftQuery.get(), rightQuery.get());
        }

    }

    private static Object query(int left) {
        return null;
    }

    public record MyResult(Object o, Object o1) {
    }



}
