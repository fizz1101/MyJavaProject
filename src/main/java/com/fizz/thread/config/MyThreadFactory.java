package com.fizz.thread.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {

    private static boolean daemon = false;

    public MyThreadFactory(boolean daemon) {
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(daemon);
        return t;
    }

}
