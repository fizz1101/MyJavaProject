package com.fizz.smgp.demo;

import java.util.*;
import java.sql.*;
import java.io.*;
import com.huawei.smproxy.SMGPSMProxy;
import com.huawei.smproxy.comm.smgp.*;
import com.huawei.smproxy.comm.smgp.message.*;
import com.huawei.smproxy.util.*;

/**
 * <p>Web发送短消息管理操作类，具体负责将页面提交的短消息发送到infoX</p>
 */

public class SMSender extends SMGPSMProxy
{
    //系统配置信息
    private static Args arg = Env.getConfig().getArgs("SMGPConnect");

    private static SMSender instance;

    public static SMSender getInstance()
    {
        if (instance==null)
        {
            instance = new SMSender();
        }
        return instance;
    }

    protected SMSender()
    {
        super(SMSender.arg);
    }

    /**
     * 当与InfoX的连接被中断时的处理
     */
    public void OnTerminate()
    {
        System.out.println("Connection have been breaked! ");
    }
    /**
     * 对SMGW主动下发的消息的处理。此例中只返回一个成功的响应。
     * @param msg 收到的消息。
     * @return 返回的相应消息。
     */
    public SMGPMessage onDeliver(final SMGPDeliverMessage msg)
    {
        if (msg.getIsReport() == 1)
        {
            System.out.println("Get a report message. " + msg.toString());
            return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
        }
        else
        {
            System.out.println("Get a deliver message. "+msg.toString());
            return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
        }
    }

    /**
     * 发送一条消息，完成真正的消息发送。
     * @param msg 待发送的消息。
     * @return true：发送成功。false：发送失败。
     */
    public boolean send(SMGPSubmitMessage msg) {
        if ( msg == null ) {
            return false;
        }
        SMGPSubmitRespMessage reportMsg = null;
        PreparedStatement stat = null;
        try {
            reportMsg = (SMGPSubmitRespMessage)super.send(msg);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
