package com.fizz.mq.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Sender {

    private static final int SEND_NUMBER = 5;

    private ConnectionFactory connectionFactory = null; //连接工厂,JMS用它来创建连接
    private Connection connection = null;
    private Session session = null; //一个发送或接收消息的线程
    private Destination destination = null; //消息发送目标
    private MessageProducer producer  = null; //消息发送者

    public void send() {
        try {
            connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = connectionFactory.createConnection();
            //启动
            connection.start();
            //获取操作连接
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            //获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue("FirstQueue");
            //创建消息发送者
            producer = session.createProducer(destination);
            //设置不持久化，此处写死，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //构造消息，此处写死，项目就是参数，或者方法获取
            sendMessage();
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage() {
        try {
            for (int i=0; i<SEND_NUMBER; i++) {
                TextMessage message = session.createTextMessage("ActiveMQ 发送的消息 " + i);
                //发送消息到目的点
                producer.send(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Sender sender = new Sender();
        sender.send();
    }

}
