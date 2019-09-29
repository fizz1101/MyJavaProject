package com.fizz.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 案例类
 */
public class Core {

    public static void main(String[] args) {
        Integer[] strArr = new Integer[]{-1, 1, 2, 8, 4, 5};
        Stream stream = Arrays.stream(strArr);
        stream = Stream.of(10L, "12", strArr);

        //循环无限流
        Stream stream1 = Stream.iterate(0, (x) -> x + 2).limit(50);

        List<Integer> list = Arrays.asList(strArr);
        list.stream().forEach(System.out::println);

        /** 流过程操作 **/
        //筛选
        /*list.stream().filter(s -> s>0).count();
        stream.distinct();
        stream.limit(5);
        stream.skip(2);


        //排序
        stream.sorted();
        list.stream().sorted((x, y) -> x.compareTo(y)).forEach(System.out::println);


        //映射
        stream.map(e -> e.toString());
        stream.flatMap(e -> {
            return Stream.of(e);
        });*/



        /** 流终止操作 **/
        //查找、匹配
        /*list.stream().allMatch(e -> e>0);
        list.stream().anyMatch(e -> e>0);
        list.stream().noneMatch(e -> e>0);
        stream.findFirst();
        stream.findAny();//仅当使用并行流时随机返回，否则与findfirst效果一致
        stream.count();*/


        //规约
//        list.stream().reduce((x, y) -> x = x + y).get().toString();


        //收集
        list.stream().sorted(Integer::compareTo).collect(Collectors.toList());



    }

}
