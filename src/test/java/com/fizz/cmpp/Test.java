package com.fizz.cmpp;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.cmpp2.CMPPConnect;
import com.fizz.cmpp.core.cmpp2.CMPPSubmit;

import java.util.Date;

public class Test {

    public static void main(String[] args) throws Exception {
        CMPP cmpp = new CMPP();
        String  USER_NAME  = "122489";
        String  PASSWORD  = "112233";
        String  IP  = "112.35.10.164";
        String  SRC  = "10657204002";
        String  SP_ID  = "122489";
        int  PORT  = 1990;
        String SERVICEID = "hv5TbliVk";
        int  status = -1 ;
        String userTel="13578923272";
        String smsContent="您本次上网密码为8900,欢迎使用!";
        final CMPPConnect connReq = new CMPPConnect();
        connReq.setSourceAddr(USER_NAME);
        connReq.setPassword(PASSWORD);
        //connReq.setTimeStamp(CMPP.getCmppDate());
        System.out.println(new Date());
        try {
            status = cmpp.connect(connReq, IP, PORT);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        if (status != CMPP.CMPP_E_SUCCESS) {
            System.out.println("连接失败："+status);
        } else {
            System.out.println("连接成功："+status);
            CMPPSubmit submit = new CMPPSubmit();
            submit.setDestTerminalId(userTel);//
            submit.setMsgLength((short) smsContent.getBytes().length);
            submit.setMsgContent(smsContent);
            submit.setRegisteredDelivery((short) 1);//是否要求返回状态确认报告：0：不需要 1：需要
            submit.setMsgLevel((short) 9);//信息级别（0~9）
            submit.setServiceId(SERVICEID);//业务标识，是数字、字母和符号的组合。
            submit.setFeeUserType((short)2);//计费用户类型字段0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对ICP/SP/EC/SI计费;3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
            submit.setFeeTerminalId("");
            submit.setMsgFmt((short) 15);//信息格式0：ASCII串3：短信写卡操作4：二进制信息8：UCS2编码15：含GB汉字  。。。。。。
            submit.setMsgSrc(SP_ID);//916073
            submit.setFeeType("01");//01：对“计费用户号码”免费02：对“计费用户号码”按条计信息费03：对“计费用户号码”按包月收取信息费04：对“计费用户号码”的信息费封顶05：对“计费用户号码”的收费是由ICP/SP/EC/SI实现
            submit.setFeeCode("0");//资费代码（以分为单位）
            //submit.setValidTime("051116000000008+");//存活有效期，格式遵循SMPP3.3协议
            //submit.setAtTime("051116000000008+");//定时发送时间，格式遵循SMPP3.3协议
            submit.setSrcId(SRC);
            submit.setDestUsrTotal((short) 1);//接收信息的用户数量(小于100个用户)
            //submit.setLinkId("");//点播业务使用的LinkID，非点播类业务的MT流程不使用该字段
            int result = cmpp.submit(submit);
            if (result == CMPP.CMPP_E_SUCCESS) {
                System.out.println("CMPP短信发送成功##########手机号码:"+ userTel + "消息内容:" + smsContent+ "网关响应结果:" + result);
            } else {
                System.out.println("CMPP短信发送失败##########手机号码:"+ userTel + "消息内容:" + smsContent+ "网关响应结果:" + result);
            }
        }
    }

}
