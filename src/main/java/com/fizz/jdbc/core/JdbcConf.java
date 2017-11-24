package com.fizz.jdbc.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JdbcConf {

    static String driverName = "";
    static String url = "";
    static String user = "";
    static String  password = "";
    static int poolSize = 5;
    static int batchSize = 100;
    static boolean initStatus = false;

    public static void init(InputStream in) {
        Properties prop = new Properties();
        try {
            prop.load(in);
            driverName = prop.getProperty("mysql.driverName", "com.mysql.jdbc.Driver");
            url = prop.getProperty("mysql.url");
            user = prop.getProperty("mysql.user", "root");
            password = prop.getProperty("mysql.password", "123456");
            poolSize = Integer.parseInt(prop.getProperty("jdbc.pool.size", "5"));
            batchSize = Integer.parseInt(prop.getProperty("jdbc.batch.size", "100"));
            initStatus = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
