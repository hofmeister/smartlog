package com.vonhof.smartlogdemo.demo;


import com.vonhof.smartlog.Logger;
import com.vonhof.smartlog.LoggerFactory;
import com.vonhof.smartlog.subscriber.JavaLoggingSubscriber;
import com.vonhof.smartlog.store.SmartLogServerStore;
import com.vonhof.smartlogdemo.Lib;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class,"demo","test");

    public static final void main(String[] args) throws InterruptedException {
        LoggerFactory.setStore(new SmartLogServerStore());
        LoggerFactory.addSubscriber(new JavaLoggingSubscriber());

        Lib lib = new Lib();

        while(true) {
            log.info("This is a demo log message");
            Thread.sleep(2000);

            lib.demo();

            Thread.sleep(1000);
        }
    }
}
