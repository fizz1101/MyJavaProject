package com.fizz.jdk8.optional;

import com.fizz.bean.DynamicBean;

import java.util.Objects;
import java.util.Optional;

/**
 * Optional 案例类
 */
public class Core {

    public static void main(String[] args) {

        DynamicBean full = new DynamicBean();
        full.setCode("9999");
        DynamicBean empty = new DynamicBean();

        DynamicBean result;
        final DynamicBean[] special = new DynamicBean[1];
        Optional<DynamicBean> optional;
        final String[] resArr = new String[1];
        String res;


        /**
         * 初始化
         *  empty()
         *  of()
         *  ofNullable()
         */
        optional = Optional.empty();
        optional = Optional.ofNullable(full);
        optional = Optional.of(empty);


        /**
         * 获取实体对象
         *  get()
         *  orElse()
         *  orElseGet()
         *  orElseThrow()
         */
        result = Optional.ofNullable(empty).get();
        result = Optional.ofNullable(empty).orElse(full);
        result = Optional.ofNullable(empty).orElseGet(() -> full);
        result = Optional.ofNullable(empty).orElseThrow(() -> new IllegalArgumentException("实体类不存在"));


        /**
         * 条件判断
         *  isPresent()
         *  ifPresent()
         *  map()
         *  flatMap()
         *  filter()
         */
        res = Optional.ofNullable(full).map(u -> u.getCode()).orElse("0000");
        res = Optional.ofNullable(full).flatMap(u -> Optional.ofNullable(u.getCode())).orElse("0000");
        Optional.ofNullable(full).ifPresent(u -> resArr[0] = u.getCode());
        Optional.ofNullable(full).filter(u -> Objects.equals(u.getCode(), "0000")).ifPresent(u -> special[0] = u);



    }

}
