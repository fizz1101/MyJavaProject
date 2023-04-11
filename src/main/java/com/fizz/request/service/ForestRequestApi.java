package com.fizz.request.service;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.annotation.*;
import com.fizz.request.EwifiRequestInterceptor;

@Address(host = "127.0.0.1", port = "80")
@BaseRequest(
        baseURL = "http://127.0.0.1/wlanPortal",
        interceptor = EwifiRequestInterceptor.class,
        connectTimeout = 3000,
        readTimeout = 5000
)
@LogEnabled(logResponseContent = true)
public interface ForestRequestApi {

    @Post("/account_manager/create")
    String ewifiAccountCreate(@JSONBody JSONObject json);

    @Post("/account_manager/update")
    String ewifiAccountUpdate(@JSONBody JSONObject json);

    @Post("/account_manager/delete")
    String ewifiAccountDelete(@JSONBody JSONObject json);

}
