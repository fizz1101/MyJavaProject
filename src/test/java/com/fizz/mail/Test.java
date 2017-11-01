package com.fizz.mail;

import com.fizz.mail.core.MailUtil;

public class Test {

    public static void main(String[] args) {
        MailUtil mail = new MailUtil();
        mail.setSender("telin-zjyd@telincn.com", "telin-zjyd@telincn.com", "TLtl12123456");
        mail.send("zhangchunzhen@quantongfu.com", "测试", "test");
    }

}
