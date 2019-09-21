package com.fizz.webservice.core;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(targetNamespace = "http://www.fizz.com")
public class WSServer {

    @WebMethod(action = "mySayAction", operationName = "say")
    @WebResult(name = "result")
    public String sayHello(@WebParam(name = "name") String name, @WebParam(name = "age") Integer age) {
        System.out.println("get Message......");
        String result = "Hello World: " + name + " " + age;
        return result;
    }

    @WebMethod
    public String test(String[] args) {
        String res = "";
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                res += args[i];
            }
        }
        System.out.println("接收参数："+res);
        return res;
    }

    public static void main(String[] args) {
        String address = "http://192.168.82.5:8099/HelloWorld";
        Object implementor = new WSServer();
        Endpoint.publish(address, implementor);
        System.out.println("server is running");
    }

}
