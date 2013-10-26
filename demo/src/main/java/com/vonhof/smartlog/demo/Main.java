package com.vonhof.smartlog.demo;


import com.vonhof.smartlog.Logger;
import com.vonhof.smartlog.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class,"demo","test");

    public static final void main(String[] args) throws InterruptedException {

        while(true) {
            log.info("This is a demo log message");
            Thread.sleep(2000);
        }
    }
}
