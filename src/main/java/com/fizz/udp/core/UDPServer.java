package com.fizz.udp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    public static void main(String[] args)throws IOException {
        //服务端在3000端口监听接收到的数据
        DatagramSocket ds = new DatagramSocket(1813);
        //接收从客户端发送过来的数据
        byte[] buf = new byte[4096];
        DatagramPacket dp_receive = new DatagramPacket(buf, buf.length);
        System.out.println("server is on，waiting for client to send data......");
        boolean f = true;
        while(f){
            //服务器端接收来自客户端的数据
            ds.receive(dp_receive);
            System.out.println("server received data from client：");
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            System.out.println(str_receive);
            //数据发动到客户端的9000端口
            /*String str_send = "Hello UDPclient";
            DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),dp_receive.getAddress(),dp_receive.getPort());
            ds.send(dp_send);*/
            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
            //所以这里要将dp_receive的内部消息长度重新置为1024
            dp_receive.setLength(4096);
        }
        ds.close();
    }

}
