package com.fizz.request.core;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.fizz.encrypt.core.DESUtil;
import com.fizz.request.service.ForestRequestApi;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForestClient {

    public static void main(String[] args) {
        ForestRequestApi client = Forest.client(ForestRequestApi.class);
        JSONObject param = new JSONObject();
        param.put("time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        param.put("username", "fizz");
        param.put("password", DESUtil.encrypt("111111", "ZMS@OxT"));
//        param.put("newPwd", DESUtil.encrypt("111111", "ZMS@OxTh"));
//        param.put("businessUserId", "1234567890");
//        param.put("groupId", "1");
//        String res = client.ewifiAccountCreate(param);

        String res = client.ewifiAccountUpdate(param);

//        String res = client.ewifiAccountDelete(param);

        System.out.println(res);
    }

}
