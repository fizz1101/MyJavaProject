package com.fizz.request;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import com.fizz.encrypt.core.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EwifiRequestInterceptor<T> implements Interceptor<T> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        JSONObject param = JSONObject.parseObject(JSONObject.toJSONString(args[0]));
        String sign = createSign(param, "ewifi_slt");
        request.addBody("sign", sign);
    }

    /**
     * 生成请求签名
     * @param paramMap
     * @param secret
     * @return
     */
    public static String createSign(JSONObject paramMap, String secret) {
        StringBuffer sb = new StringBuffer();
        paramMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        });
        sb.append("key=" + secret);
        String sign = MD5Util.get32MD5Upper(sb.toString());
        return sign;
    }

}
