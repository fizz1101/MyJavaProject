package com.fizz.smgp.demo;

import com.huawei.smproxy.SMGPSMProxy ;
import com.huawei.smproxy.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPDeliverRespMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPMessage;
import com.huawei.smproxy.util.Args;

/**
 * 接收下发消息的Demo类。
 * 可以直接使用SMGPSMProxy类提供的Send、close和getConnState方法,
 * CP有接收ISMG下发的短信的要求或ISMG断开连接的时候要求
 * 得到事件通知的时候，声明一个新的类继承SMGPSMProxy，重载实现
 * onDeliver( )和onTerminate( )。
 */

public class MySMGPSMProxy extends SMGPSMProxy
{

    public MySMGPSMProxy(Args args)
    {
        //调用父类的构造函数，完成初始化和登录ISMG的功能，不能省略
        super(args);
    }

    public SMGPMessage onDeliver(final SMGPDeliverMessage msg)
    {
        byte[] msgId = msg.getMsgId();

        //添加收到短消息中心下发消息的处理代码
        int result = 0;

        //实际上是返回响应消息，一定要有
        return new SMGPDeliverRespMessage(msgId,result);
    }

    /**
     * 当与InfoX的连接被中断时的处理
     */
    public void OnTerminate()
    {
        System.out.println("Connection have been breaked! ");
    }
}
