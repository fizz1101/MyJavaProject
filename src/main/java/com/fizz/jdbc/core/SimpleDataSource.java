package com.fizz.jdbc.core;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

public class SimpleDataSource implements DataSource {

//    private static int poolSize = 5;// 默认为5个
//
//    private static String mysql_driverName = "";
//    private static String mysql_url = "";
//    private static String mysql_user = "";
//    private static String mysql_password = "";

    private LinkedList<Connection> pool = new LinkedList<>();

    static {
        if (!JdbcConf.initStatus) {
            InputStream in = SimpleDataSource.class.getClassLoader().getResourceAsStream("config/jdbc/jdbc.properties");
            JdbcConf.init(in);
        }
    }

    public SimpleDataSource() {
        this(JdbcConf.driverName, JdbcConf.url, JdbcConf.user, JdbcConf.password, JdbcConf.poolSize);
    }

    public SimpleDataSource(String driver, String url) {
        this(driver, url, JdbcConf.user, JdbcConf.password, JdbcConf.poolSize);
    }

    public SimpleDataSource(String url, String name, String pwd) {
        this(JdbcConf.driverName, url, name, pwd);
    }

    public SimpleDataSource(String driver, String url, String name, String pwd) {
        this(driver, url, name, pwd, JdbcConf.poolSize);
    }

    /**
     * 初始化数据库连接配置
     * @param in    连接参数资源文件
     *              mysql.driverName: 驱动名称
     *              mysql.url: 连接地址
     *              mysql.user: 用户名
     *              mysql.password: 密码
     *              jdbc.pool.size: 连接池数量
     *              jdbc.batch.size: 批量操作数量
     */
    public SimpleDataSource(InputStream in) {
        JdbcConf.init(in);
    }

    @SuppressWarnings("static-access")
    public SimpleDataSource(String driver, String url, String name, String pwd, int poolSize) {
        try {
            Class.forName(driver);
            JdbcConf.poolSize = poolSize;
            if (poolSize <= 0) {
                throw new RuntimeException("初始化池大小失败: " + poolSize);
            }
            for (int i = 0; i < poolSize; i++) {
                Connection con = DriverManager.getConnection(url, name, pwd);
                con = ConnectionProxy.getProxy(con, pool);// 获取被代理的对象
                pool.add(con);// 添加被代理的对象
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /** 获取池大小 */
    public int getPoolSize() {
        return JdbcConf.poolSize;
    }

    /** 不支持日志操作 */
    public PrintWriter getLogWriter() throws SQLException {
        throw new RuntimeException("Unsupport Operation.");
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new RuntimeException("Unsupport operation.");
    }

    /** 不支持超时操作 */
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new RuntimeException("Unsupport operation.");
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    /** 从池中取一个连接对象,使用了同步和线程调度 */
    public Connection getConnection() throws SQLException {
        synchronized (pool) {
            if (pool.size() == 0) {
                try {
                    pool.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return getConnection();
            } else {
                return pool.removeFirst();
            }
        }
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new RuntimeException("不支持接收用户名和密码的操作");
    }

    /** 实现对Connection的动态代理 */
    static class ConnectionProxy implements InvocationHandler {

        private Object obj;
        private LinkedList<Connection> pool;

        private ConnectionProxy(Object obj, LinkedList<Connection> pool) {
            this.obj = obj;
            this.pool = pool;
        }

        public static Connection getProxy(Object o, LinkedList<Connection> pool) {
            Object proxed = Proxy.newProxyInstance(o.getClass().getClassLoader(), new Class[] { Connection.class },
                    new ConnectionProxy(o, pool));
            return (Connection) proxed;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("close")) {
                synchronized (pool) {
                    pool.add((Connection) proxy);
                    pool.notify();
                }
                return null;
            } else {
                return method.invoke(obj, args);
            }
        }

    }

    public Logger getParentLogger() {
        // TODO Auto-generated method stub
        return null;
    }

}
