package org.example;

import java.util.concurrent.StructuredTaskScope;

public class ScreenShotsLeft {

    // Imperative style, to the left on slide
    public static class Nested {

        private MyResult getResult(int left, int right) {

            var leftQuery = query(left);
            var rightQuery = query(right);



            return new MyResult(leftQuery, rightQuery);
        }

        private Object query(int left) {
            return null;
        }

        public record MyResult(Object o, Object o1) {
        }


        public DemoJavaDoc.MyResult scopeForScreenshot(int left, int right) throws InterruptedException {

            try (var scope = StructuredTaskScope.open()) {

                var leftQuery = scope.fork(() -> query(left));
                var rightQuery = scope.fork(() -> query(right));

                scope.join();

                return new DemoJavaDoc.MyResult(leftQuery.get(), rightQuery.get());
            }
        }

        public static void main(String[] args) {
            // make method name blue :D
            var x = new Nested().getResult(1, 2);
        }
    }


}
