package com.vonhof.smartlog;

public class SmartLog {

    private static SmartLogInstance instance = new SmartLogInstance();

    public static SmartLogInstance getInstance() {
        return instance;
    }

    public static void setInstance(SmartLogInstance instance) {
        SmartLog.instance = instance;
    }

    public static Logger getLogger(Class clz, String ... tags) {
        return new Logger(getInstance(), clz,tags);
    }
}
