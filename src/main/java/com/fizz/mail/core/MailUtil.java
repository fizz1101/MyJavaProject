package com.fizz.mail.core;

import com.fizz.jdbc.core.SimpleDataSource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailUtil {

    private static String host = "smtp.exmail.qq.com"; // smtp服务器
    private static String from = "telin-zjyd@telincn.com"; // 发件人地址
    private static String to = "zhanglikang@quantongfu.com"; // 收件人地址
    private static String user = "telin-zjyd@telincn.com"; // 用户名
    private static String pwd = "TLtl12123456"; // 密码
    private static String affix = ""; // 附件地址
    private static String affixName = ""; // 附件名称

    static {
        Properties prop =  new Properties();
        InputStream in = SimpleDataSource.class.getClassLoader().getResourceAsStream( "config/mail/mail.properties" );
        try {
            prop.load(in);
            host = prop.getProperty("mail.host");
            from = prop.getProperty("mail.from");
            to = prop.getProperty("mail.to");
            user = prop.getProperty("mail.from");
            pwd = prop.getProperty("mail.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MailUtil() {

    }

    public void setSender(String from, String user, String pwd) {
        this.from = from;
        this.user = user;
        this.pwd = pwd;
    }

    public void setAffix(String affix, String affixName) {
        this.affix = affix;
        this.affixName = affixName;
    }

    public void send(String toAddrs, String subject, String content) {
        Properties props = new Properties();

        // 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
        props.put("mail.smtp.host", host);
        // 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        props.put("mail.smtp.auth", "true");

        // 用刚刚设置好的props对象构建一个session
        Session session = Session.getDefaultInstance(props);

        // 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
        // 用（你可以在控制台（console)上看到发送邮件的过程）
        session.setDebug(true);

        // 用session为参数定义消息对象
        MimeMessage message = new MimeMessage(session);
        try {
            // 加载发件人地址
            message.setFrom(new InternetAddress(from));
            // 加载收件人地址
            if (toAddrs != null && !"".equals(toAddrs)) {
                to = toAddrs;
            }
            String[] arr = to.split(",");
            for (int i=0; i<arr.length; i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(arr[i]));
            }
            // 加载标题
            message.setSubject(subject);

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 设置邮件的文本内容
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);
            multipart.addBodyPart(contentPart);
            // 添加附件
            if (affix != null && !"".equals(affix)) {
                BodyPart messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(affix);
                // 添加附件的内容
                messageBodyPart.setDataHandler(new DataHandler(source));
                // 添加附件的标题
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                messageBodyPart.setFileName("=?GBK?B?"
                        + enc.encode(affixName.getBytes()) + "?=");
                multipart.addBodyPart(messageBodyPart);
            }

            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();
            // 发送邮件
            Transport transport = session.getTransport("smtp");
            // 连接服务器的邮箱
            transport.connect(host, user, pwd);
            // 把邮件发送出去
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
