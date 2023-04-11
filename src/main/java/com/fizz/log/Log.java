package com.fizz.log;

import org.apache.log4j.Logger;

public class Log {

    private static Logger logger = Logger.getLogger(Log.class);

    public static void main(String[] args) {
        logger.error("aaa");
    }

}
