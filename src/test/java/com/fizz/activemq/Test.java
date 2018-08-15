package com.fizz.activemq;

import com.fizz.mq.activemq.Receiver;
import com.fizz.mq.activemq.Sender;

public class Test {

    public static void main(String[] args) {
        Sender sender = new Sender();
        sender.send();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Receiver receiver = new Receiver();
        receiver.receive();
    }

}
