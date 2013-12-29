package com.vonhof.smartlog.slf4j;

import com.vonhof.smartlog.SmartLog;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SmartLogLoggerFactory implements ILoggerFactory {

    @Override
    public Logger getLogger(String name) {
        try {
            Class<?> clz = Class.forName(name);
            com.vonhof.smartlog.Logger smartLogger = SmartLog.getLogger(clz);
            return new SmartLogLoggerAdapter(smartLogger);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Smart log requires fully-qualified class names", e);
        }
    }
}
