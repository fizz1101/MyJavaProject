package com.fizz.smgp.demo;

import java.util.*;
import java.sql.*;
import java.io.*;
import com.huawei.smproxy.SMGPSMProxy;
import com.huawei.smproxy.comm.smgp.*;
import com.huawei.smproxy.comm.smgp.message.*;
import com.huawei.smproxy.util.*;

public class SMReceiver extends SMGPSMProxy
        implements Runnable{
  //系统配置信息
  private static Args arg = Env.getConfig().getArgs("SMGPConnect");

  private static Thread instance;

  public static Thread getInstance() {
    if (instance == null) {
      instance = new Thread(new SMReceiver());
    }
    return instance;
  }

  protected SMReceiver() {
    super(SMReceiver.arg);
  }

  /**
   * 连接终止的处理，由API使用者实现
   * 短信中心连接终止后，需要执行动作的接口
   */
  public void OnTerminate() {
    System.out.println("Connection have been breaked! ");
  }

  /**
   * 对SMGW主动下发的消息的处理接口。此例中只返回一个成功的响应。
   * @param msg 收到的消息。
   * @return 返回的相应消息。
   */
  public SMGPMessage onDeliver(final SMGPDeliverMessage msg) {

    if(msg.getIsReport() != 1){
      System.out.println("\n**************************Received a new message!***************************");
      System.out.println(msg.toString());
      System.out.println("The Sender is: " + msg.getSrcTermID());
      System.out.println("***************************End new message! **************************\n");
/*
      String[] rcvMobile = new String[1];
      rcvMobile[0] = msg.getSrcTermID();

      //生成一个CNGP回复消息
      System.out.print("Create new reply...\n");

      CNGPSubmitMessage reply = new CNGPSubmitMessage(
          "3001999995", //spid
          1, //subtype
          1, //needReport,
          3, //priority
          "+xkx", //serviceId
          "01", //FeeType
          0, //FeeUserType
          "000000", //FeeCode
          15, //msgFormat
          null,
          null, //定时发送时间(null:立即发送)
          "94005", //srcTermId
          "94005", //ChargeTermId
          1, //destTermIdCount
          rcvMobile, //destTermId
          16, //msgLength
          "你好，谢谢使用！", //msgmsgContent
          0 //protocolValue
          );

      if (send(reply)) {
        System.out.println("\nThe reply send OK!\n");
      }
      else {
        System.out.println("\nThe reply send Fail!\n");
      }
*/
    }else{
      System.out.println("\n++++++++++++++++++++++++++Received a new report!+++++++++++++++++++++++++");
      System.out.println(msg.toString());
      System.out.println("++++++++++++++++++++++++++End a new report!+++++++++++++++++++++++++\n");
    }
    return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
  }

  /**
   * 封装父类的send()方法
   * 发送一条消息，完成真正的消息发送。
   * @param msg 待发送的消息。
   * @return true：发送成功。false：发送失败。
   */
  /*
  public boolean send(CNGPSubmitMessage msg) {
    if (msg == null) {
      return false;
    }
    CNGPSubmitRespMessage reportMsg = null;
    PreparedStatement stat = null;
    try {
      reportMsg = (CNGPSubmitRespMessage)super.send(msg);
      //System.out.println("发送状态:  " + reportMsg.toString());
    }
    catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }catch (java.lang.NullPointerException e) {
      return false;
    }
    return true;
  }*/
  public boolean send(SMGPSubmitMessage msg) {
    if (msg == null) {
      return false;
    }
    try{
      super.send(msg);
    }catch(IOException e){
    }
    return true;
  }

  public void run(){
    while(true){
      try {
        Thread.sleep(500);
      }
      catch (Exception ex) {}
    }
  }

  //测试用主函数
  public static void main(String[] args) {
    //生成100个接收短信的手机号码

    //发送消息(发送多次)
    Thread receiver = SMReceiver.getInstance();
    receiver.start();
  }

}
