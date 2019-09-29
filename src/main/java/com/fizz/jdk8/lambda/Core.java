package com.fizz.jdk8.lambda;

public class Core {

    public static void main(String[] args) {
        func(System.out::println);
        func(x -> System.out.println("Hello World " + x));
        boolean flag = func1((x) -> {
            System.out.println("Hello World " + x);
            return true;
        });
    }

    private static void func(FunctionInterface functionInterface) {
        String key = "fizz";
        functionInterface.test(key);
    }

    private static boolean func1(FunctionInterface1 functionInterface1) {
        String key = "zcz";
        return functionInterface1.test(key);
    }



    @FunctionalInterface
    interface FunctionInterface {
        void test(String key);
    }

    @FunctionalInterface
    interface FunctionInterface1 {
        boolean test(String key);
    }

}
