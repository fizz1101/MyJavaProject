package com.fizz.udp.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 1;      //设置重发数据的最多次数
    public static void main(String args[])throws IOException {
        //客户端在9000端口监听接收到的数据
        DatagramSocket ds = new DatagramSocket(9000);
        InetAddress loc = InetAddress.getLocalHost();
        //wifiyw.10010sh.cn
        loc = InetAddress.getByName("192.168.82.6");
        //定义用来发送数据的DatagramPacket实例
        String mac_trigger_check = "01 30 00 00 a6 ed 00 00 0a 09 c7 0e 00 00 00 03 0b 08 8c eb c6 0e 2f 28 0a 06 ca 60 7c f6 30 17 4e 42 44 58 2d 69 4e 69 6e 67 62 6f 2d 43 52 31 36 30 31 30 46";
        String mac_trigger_binding = "01 32 00 00 3e ce 00 00 0a 09 c7 0e 00 00 00 07 0b 08 8c eb c6 0e 2f 28 0a 06 ca 60 7c f6 01 1f 31 38 38 36 38 36 36 31 33 35 36 66 30 31 30 61 36 30 38 64 64 63 39 34 65 36 66 39 30 30 17 4e 42 44 58 2d 69 4e 69 6e 67 62 6f 2d 43 52 31 36 30 31 30 46 50 32 73 6c 6f 74 3d 33 3b 73 75 62 73 6c 6f 74 3d 31 3b 70 6f 72 74 3d 31 3b 76 6c 61 6e 69 64 3d 31 30 39 34 3b 76 6c 61 6e 69 64 32 3d 31 31 35 3b 31 06 5c 0f e3 f4 34 a4 4d 6f 7a 69 6c 6c 61 2f 35 2e 30 28 4c 69 6e 75 78 3b 41 6e 64 72 6f 69 64 38 2e 30 3b 46 52 44 2d 41 4c 31 30 42 75 69 6c 64 2f 48 55 41 57 45 49 46 52 44 2d 41 4c 31 30 29 41 70 70 6c 65 57 65 62 4b 69 74 2f 35 33 37 2e 33 36 28 4b 48 54 4d 4c 2c 6c 69 6b 65 47 65 63 6b 6f 29 56 65 72 73 69 6f 6e 2f 34 2e 30 43 68 72 6f 6d 65 2f 33 37 2e 30 2e 30 2e 30 4d 6f 62 69 6c 65 4d 51 51 42 72 6f 77 73 65 72 2f 37 2e 33 54 42 53 2f 30 33 37 33 32 34 53 61 66 61 72 69 2f 35 33 37 2e 33 36";

        String str_send = mac_trigger_binding;
        str_send = str_send.replaceAll("\\s+", "");
        byte[] b = getHexBytes(str_send);
//        String str = "test";
//        b = str.getBytes();
        DatagramPacket dp_send= new DatagramPacket(b,b.length,loc,50100);
        //定义用来接收数据的DatagramPacket实例
        byte[] buf = new byte[1024];
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        //数据发向本地3000端口
        ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
        int tries = 0;                         //重发数据的次数
        boolean receivedResponse = false;     //是否接收到数据的标志位
        //直到接收到数据，或者重发次数达到预定值，则退出循环
        while(!receivedResponse && tries<MAXNUM){
            //发送数据
            ds.send(dp_send);
            try{
                //接收从服务端发送回来的数据
                ds.receive(dp_receive);
                //如果接收到的数据不是来自目标地址，则抛出异常
                if(!dp_receive.getAddress().equals(loc)){
                    throw new IOException("Received packet from an umknown source");
                }
                //如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
                receivedResponse = true;
            }catch(InterruptedIOException e){
                //如果接收数据时阻塞超时，重发并减少一次重发的次数
                tries += 1;
                System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );
            }
        }
        if(receivedResponse){
            //如果收到数据，则打印出来
            System.out.println("client received data from server：");
            String str_receive = "'" + new String(dp_receive.getData(),0,dp_receive.getLength()) + "'" +
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            System.out.println(str_receive);
            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
            //所以这里要将dp_receive的内部消息长度重新置为1024
            dp_receive.setLength(1024);
        }else{
            //如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
            System.out.println("No response -- give up.");
        }
        ds.close();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String hexStr2Str(String hexStr) {

        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static byte[] getHexBytes(String str){
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

}
