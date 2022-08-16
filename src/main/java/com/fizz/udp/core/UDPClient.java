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
//        loc = InetAddress.getByName("192.168.83.37");
        //定义用来发送数据的DatagramPacket实例
        //portal
        String portal_challenge_request = "02 01 00 00 06 00 00 00 7b 9d a7 3b 00 00 00 00 96 01 8f e6 88 e3 89 4a cf 0c f6 3a df ab 5f e4";

        //radius access
        String radius_access_request = "01 b9 01 85 2d 70 be 3b 90 32 9c fe d7 93 e3 ec 02 e2 97 36 01 1f 31 33 35 36 36 38 39 33 37 32 35 36 62 61 33 38 39 65 37 65 32 31 38 34 32 36 30 62 31 03 13 a4 9b e7 79 4c bf c6 4f ef da 62 57 ce 2c c7 f3 6a 3c 12 2d 70 be 3b 90 32 9c fe d7 93 e3 ec 02 e2 97 36 05 06 01 00 2f 14 04 06 3c 0c 47 2d 06 06 00 00 00 02 07 06 00 00 00 01 08 06 7b 9d a7 3b 1f 13 37 30 3a 34 37 3a 65 39 3a 37 30 3a 63 35 3a 38 39 20 16 37 36 30 32 2e 30 35 37 36 2e 35 37 31 2e 30 30 2e 34 36 30 3d 06 00 00 00 13 57 21 65 74 68 20 31 2f 30 2f 32 3a 34 30 39 36 2e 33 38 36 30 20 30 2f 30 2f 30 2f 30 2f 30 2f 30 1e 25 30 30 2d 30 30 2d 30 30 2d 30 30 2d 30 30 2d 30 30 3a 69 2d 7a 68 65 6a 69 61 6e 67 2d 75 6e 69 63 6f 6d 2c 23 54 5a 2d 4c 48 5a 4a 30 31 32 30 32 33 38 36 30 30 30 30 30 30 63 38 39 38 37 31 32 31 37 32 34 37 4d 0c 31 30 30 30 30 30 30 30 30 30 1a 6b 00 00 07 db 3b 06 5f e4 c0 42 3c 22 31 32 33 2e 31 35 37 2e 31 36 37 2e 35 39 20 37 30 3a 34 37 3a 65 39 3a 37 30 3a 63 35 3a 38 39 1a 06 00 03 50 9f fe 0d 48 75 61 77 65 69 20 4d 45 36 30 ff 06 4d 45 36 30 8a 11 69 5f 7a 68 65 6a 69 61 6e 67 5f 74 65 73 74 99 13 37 30 3a 34 37 3a 65 39 3a 37 30 3a 63 35 3a 38 39";
        radius_access_request = "01 06 00 ec 16 76 0f 1e 30 7b 0e 96 4d 08 72 33 5c 18 4e 96 01 1f 31 35 39 35 37 31 36 37 38 34 37 39 37 38 33 33 61 65 35 61 30 35 31 34 34 36 38 38 39 03 13 e6 39 67 be 9f 07 37 c9 f0 69 b3 4f a4 5d 40 02 ef 3c 12 38 b2 02 8a 17 a2 3c ff 54 72 51 2d 7c db 00 53 06 06 00 00 00 08 07 06 00 00 00 00 05 06 00 00 8a 01 3d 06 00 00 00 13 1f 13 32 43 3a 32 30 3a 30 42 3a 32 31 3a 45 45 3a 36 46 20 05 33 30 30 04 06 3a f7 f9 aa 1e 1d 41 45 2d 37 39 2d 36 37 2d 30 33 2d 34 44 2d 31 31 3a 46 58 53 57 2d 46 52 45 45 21 08 6f 72 69 67 69 6e 1a 0e 00 00 0f 3e 06 08 2c 20 0b 21 ee 6f 1a 0a 00 00 0f 3e 14 04 01 2c 57 21 73 6c 6f 74 3d 31 37 3b 6d 6f 64 3d 32 3b 70 6f 72 74 3d 31 3b 76 6c 61 6e 69 64 3d 33 30 30";
        //mac_trigger
        String mac_trigger_check = "01 30 00 00 a6 ed 00 00 0a 09 c7 0e 00 00 00 03 0b 08 8c eb c6 0e 2f 28 0a 06 ca 60 7c f6 30 17 4e 42 44 58 2d 69 4e 69 6e 67 62 6f 2d 43 52 31 36 30 31 30 46";
        String mac_trigger_binding = "01 32 00 00 3e ce 00 00 0a 09 c7 0e 00 00 00 07 0b 08 8c eb c6 0e 2f 28 0a 06 ca 60 7c f6 01 1f 31 38 38 36 38 36 36 31 33 35 36 66 30 31 30 61 36 30 38 64 64 63 39 34 65 36 66 39 30 30 17 4e 42 44 58 2d 69 4e 69 6e 67 62 6f 2d 43 52 31 36 30 31 30 46 50 32 73 6c 6f 74 3d 33 3b 73 75 62 73 6c 6f 74 3d 31 3b 70 6f 72 74 3d 31 3b 76 6c 61 6e 69 64 3d 31 30 39 34 3b 76 6c 61 6e 69 64 32 3d 31 31 35 3b 31 06 5c 0f e3 f4 34 a4 4d 6f 7a 69 6c 6c 61 2f 35 2e 30 28 4c 69 6e 75 78 3b 41 6e 64 72 6f 69 64 38 2e 30 3b 46 52 44 2d 41 4c 31 30 42 75 69 6c 64 2f 48 55 41 57 45 49 46 52 44 2d 41 4c 31 30 29 41 70 70 6c 65 57 65 62 4b 69 74 2f 35 33 37 2e 33 36 28 4b 48 54 4d 4c 2c 6c 69 6b 65 47 65 63 6b 6f 29 56 65 72 73 69 6f 6e 2f 34 2e 30 43 68 72 6f 6d 65 2f 33 37 2e 30 2e 30 2e 30 4d 6f 62 69 6c 65 4d 51 51 42 72 6f 77 73 65 72 2f 37 2e 33 54 42 53 2f 30 33 37 33 32 34 53 61 66 61 72 69 2f 35 33 37 2e 33 36";

        String test_syslog = "03040016628dc89ef8d10b7c00000000110204000a04feb4df5c0b74d38c0dbcd38c0dbcf67d831100350035628dc861628dc89e0000000100000079000000010000003d000000000000000000000000110204000a04feb4df5c0b74d38c0dbcd38c0dbc1c32830300350035628dc861628dc89e0000000100000079000000010000003d000000000000000000000000110204000a04f03bdf5c0b73d38c0dbcd38c0dbc7bf374f800350035628dc861628dc89e000000010000009d0000000100000046000000000000000000000000110204046f21de066f21de06df5c0b740a056e2c13f713f749000748628dc860628dc89e00000006000002e8000000040000027c000000000000000000000000060104000a059754df5c0b74310411be310411bea9983f6301bb01bb628dc76e628dc89e0000000b0000133c0000000b000006c8000000000000000000000000060104000a053d35df5c0b75b7f6bda7b7f6bda7b374f6b400500050628dc89b628dc89e000000020000005c00000004000000b4000000000000000000000000060104000a053d35df5c0b75b7f6bda4b7f6bda4ca78f6b200500050628dc89b628dc89e000000020000005c00000004000000b4000000000000000000000000060104000a053d35df5c0b75701139ad701139ad8584f67c00500050628dc89b628dc89e000000030000008400000004000000b4000000000000000000000000060104000a056edddf5c0b75249bdfd5249bdfd5945cf66400500050628dc89a628dc89e0000001a000073f20000000b000001cc000000000000000000000000110204000a05357fdf5c0b7770113c6170113c6190bff1851f401f40628dc893628dc89e000000020000050800000003000009e9000000000000000000000000110804000a056ab5df5c0b756bccca736bccca731f72f718921e921e628dc89e628dc89e00000000000000000000000100000074000000000000000000000000110804000a04feb4df5c0b74d38c0dbcd38c0dbc595b986100350035628dc89e628dc89e0000000000000000000000010000003d000000000000000000000000110804000a056ab5df5c0b75aee4cb73aee4cb731f72f71e1a1e1a1e628dc89e628dc89e00000000000000000000000100000074000000000000000000000000060804000a059a10df5c0b70792475677924756791cec30d01bb01bb628dc89e628dc89e00000000000000000000000100000047000000000000000000000000060804000a055457df5c0b777905606679056066d47af2ac01bb01bb628dc89e628dc89e0000000000000000000000010000003c000000000000000000000000060804000a059094df5c0b7476c2217976c221799e509872192f192f628dc89e628dc89e0000000000000000000000010000003c000000000000000000000000110804000a059094df5c0b74d38c0dbcd38c0dbc0aaa987700350035628dc89e628dc89e0000000000000000000000010000004b000000000000000000000000060804000a0548a3df5c0b73ddb0018cddb0018cd09e816c1f901f90628dc89e628dc89e0000000000000000000000010000003c000000000000000000000000060804e09f5990eb9f5990ebb7f67006b7f67006d9b0d9b01fff1fff628dc89e628dc89e00000000000000000000000100000028000000000000000000000000110804000a04feb4df5c0b74d38c0dbcd38c0dbc9757987f00350035628dc89e628dc89e0000000000000000000000010000003d00000000000000000000000011080404278016bb278016bbdf5c0b740a056e2c963f963f49000748628dc89e628dc89e000000000000000000000001000000a8000000000000000000000000110804000a056e2cdf5c0b7478842f1778842f17c39a98920d960d96628dc89e628dc89e00000000000000000000000100000030000000000000000000000000";

        String str_send = test_syslog;
        str_send = str_send.replaceAll("\\s+", "");
        byte[] b = getHexBytes(str_send);
//        String str = "<160>2019-09-20 13:55:13.491 powerac: [U_EVENT] <EMERG> (1035) sta_mac:4c-49-e3-e8-e6-55,sta_ip:0.0.0.0,ap_mac:00-34-cb-64-dc-58,ssid:HC,ap_name:AP35,state:online";
//        b = str.getBytes();
        DatagramPacket dp_send= new DatagramPacket(b, b.length, loc, 515);
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
