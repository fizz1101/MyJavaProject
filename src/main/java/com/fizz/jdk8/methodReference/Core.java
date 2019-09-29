package com.fizz.jdk8.methodReference;

import java.util.Arrays;

/**
 * 方法引用 案例类
 */
public class Core {

    private String name;

    private Integer score;

    public void setNameAndScore(String name, Integer score)
    {
        this.name = name;
        this.score = score;
        System.out.println("Core "+  name +"'s score is " + score);
    }

    public void println(String s)
    {
        System.out.println(s);
    }

    public static void main(String[] args)
    {
        Core test = new Core();
        // lambda表达式使用：
        //Arrays.asList(new String[] {"a", "c", "b"}).stream().forEach(s -> test.println(s));
        // 特定对象的实例方法引用：
        Arrays.asList(new String[] {"a", "c", "b"}).stream().forEach(test::println);


        //lambda表达式的用法：
        //TestInterface testInterface = (core, name, score) -> core.setNameAndScore(name, score);
        //类的任意对象的实例方法引用的用法:
        TestInterface testInterface = Core::setNameAndScore;
        testInterface.set(new Core(), "Fizz", 100);
    }

    @FunctionalInterface
    interface TestInterface
    {
        // 注意：入参比Core类的setNameAndScore方法多1个Core对象，除第一个外其它入参类型一致
        public void set(Core d, String name, Integer score);
    }

}
