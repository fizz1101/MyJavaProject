package com.fizz.bean;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DynamicBean {

    private String code; //状态码
    private String desc; //结果描述
    private Object object  = null; //动态生成的类
    private BeanMap beanMap = null; //存放属性名称以及属性的类型

    public DynamicBean() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public DynamicBean(Map propertyMap) {
        this.object = generateBean(propertyMap);
        this.beanMap = BeanMap.create(this.object);
    }

    /**
     * 给bean属性赋值
     *
     * @param property 属性名
     * @param value 值
     */
    public void setValue(Object property, Object value) {
        beanMap.put(property, value);
    }

    /**
     * 通过属性名得到属性值
     *
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * 得到该实体bean对象
     *
     * @return
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * @param propertyMap
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Object generateBean(Map propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        Set keySet = propertyMap.keySet();
        for (Iterator i = keySet.iterator(); i.hasNext();) {
            String key = (String) i.next();
            generator.addProperty(key, (Class) propertyMap.get(key));
        }
        return generator.create();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
