package com.fizz.request.core;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpURLConnectionUtil {

    public void http() throws  Exception {
        //1.创建HttpURLConnection连接
        URL url = new URL("aaa");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection conn = (HttpURLConnection) urlConnection;
        //2.设置HttpURLConnection参数
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        conn.connect();
        //3.URLConnection建立连接
        OutputStream outStrm = conn.getOutputStream();
        //4.HttpURLConnection发送请求
        ObjectOutputStream objOutStrm = new ObjectOutputStream(outStrm);
        objOutStrm.writeObject(new String("我是测试数据"));
        objOutStrm.flush();
        objOutStrm.close();
        //5.获取HttpURLConnection请求响应

    }

}
