package com.fizz.bean;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Analysis {

    public static void main(String[] args) {
        String jsonStr = "{\"code\":\"200\",\"desc\":\"success\",\"username\":\"fizz\",\"password\":\"123456\",\"object\":{\"username\":\"f\",\"password\":\"1\"}}";
        Map<String, Class> contentMap = new HashMap<>();
        contentMap.put("object", BizContent.class);
        System.out.println(contentMap);
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        DynamicBean dynamicBean = (DynamicBean) JSONObject.toBean(jsonObj, DynamicBean.class, contentMap);
        System.out.println(dynamicBean);
    }

}
