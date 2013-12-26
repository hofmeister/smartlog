package com.vonhof.smartlogdemo.demo;


import com.vonhof.smartlog.Logger;
import com.vonhof.smartlog.SmartLog;
import com.vonhof.smartlog.subscriber.JavaLoggingSubscriber;
import com.vonhof.smartlogdemo.Lib;

public class Main {
    private static final Logger log = SmartLog.getLogger(Main.class, "demo", "test");

    public static final void main(String[] args) throws InterruptedException {
        SmartLog.getInstance().setDebug(true);
        SmartLog.getInstance().addSubscriber(new JavaLoggingSubscriber());

        Lib lib = new Lib();

        while(true) {
            log.info("This is a demo log message");
            Thread.sleep(2000);

            lib.demo();

            Thread.sleep(1000);
        }
    }
}
