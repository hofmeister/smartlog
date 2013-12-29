package com.vonhof.smartlog.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

    @Override
    public ILoggerFactory getLoggerFactory() {
        return new SmartLogLoggerFactory();
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return SmartLogLoggerFactory.class.getName();
    }
}
