import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Ttt {

    private static final Logger LOGGER = LoggerFactory.getLogger(Ttt.class);


    public static Object lock = new Object();

    public static boolean flag = true;


    /**
     * 1、建立服务Socket
     * 2、获取输出流，把数据变成字节数组， 通过输出流发送给服务端。
     * 3、关闭输出流，获取输入流，获取反馈信息
     * 4、关闭资源
     *
     * @param //message
     * @throws IOException
     */
    public static Object sendSingle(String ip, int port, String messgae) {

        try {
            //创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket(ip, port);

            Thread thread = new Thread(new ClientThread(socket,messgae));
            thread.start();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //建立连接后，获取输出流，向服务器端发送信息
            //        OutputStream os = socket.getOutputStream();
//            //输出流包装为打印流
//            PrintWriter pw = new PrintWriter(os);
//            //向服务器端发送信息
//            pw.write("用户名：zzh;密码：123");//写入内存缓冲区
//            pw.flush();//刷新缓存，向服务器端输出信息

            //      os.write(messgae.getBytes("UTF-8"));

            //      socket.shutdownOutput();//关闭输出流

            Thread sendThread = new Thread(new SendThread(socket));
            sendThread.start();


            //获取输入流，接收服务器端响应信息
//            InputStream is = socket.getInputStream();
//
//            byte[] buf = new byte[1024];
//            int len = 0;
//            while ((len = is.read(buf)) != -1){
//                System.out.print("我是客户端，服务器端提交信息为:"+new String(buf, 0, len));
//            }

            //BufferedReader它可以包装字符流,拥有8192字符的缓冲区,将字符流放入缓存里,先把字符读到缓存里,到缓存满了或者你flush的时候,再读入内存,就是为了提供读的效率而设计的。用法://接收数据
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));//BufferReader自带缓冲区
//            String data = null;
//            while ((data = br.readLine()) != null) {
//                System.out.println("我是客户端，服务器端提交信息为：" + data);
//            }

            //关闭其他资源
//            br.close();
//            is.close();
//            pw.close();
//            os.close();
            // socket.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

    private static class ClientThread implements Runnable{

        private final String message;
        private Socket socket;

        public ClientThread(Socket socket, String messgae) {
            this.socket = socket;
            this.message = messgae;
        }

        @Override
        public void run() {

            try {
                System.out.println("=====================开始发送心跳包==============");
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println("发送心跳数据包");
                    LOGGER.info("发送心跳数据包...");

                    OutputStream outStr = socket.getOutputStream();
                    outStr.write("send heart beat data package !".getBytes("UTF-8"));
//                    outStr.write(message.getBytes("UTF-8"));
                    outStr.flush();
                    socket.shutdownOutput();


                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }


    private static class SendThread implements Runnable {

        private Socket socket;//持有socket

        public SendThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                System.out.println("==============开始接收数据===============");

                InputStream inStr = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = inStr.read(buf)) != -1) {
                    System.out.print("我是客户端，服务器端提交信息为:" + new String(buf, 0, len));
                }
                inStr.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }

    public static void main(String[] args) {
        new Thread("b") {
            public void run() {
                int count = 1;
//                while (true) {
                    try {
                        Thread.sleep(10);
                        System.out.print(count++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Object obj = Ttt.sendSingle("192.168.82.15", 4444, "当前报警为火警");
//                }
            }
        }.start();

    }

}
