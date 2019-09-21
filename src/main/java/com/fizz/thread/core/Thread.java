package com.fizz.thread.core;

import com.fizz.thread.config.MyThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Thread {

    public static void newDaemonThread(boolean daemon) {
        ThreadFactory factory = new MyThreadFactory(daemon);
        ExecutorService executorService = Executors.newFixedThreadPool(5, factory);
        executorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

}
