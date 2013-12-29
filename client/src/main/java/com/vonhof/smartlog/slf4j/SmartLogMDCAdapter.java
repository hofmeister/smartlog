package com.vonhof.smartlog.slf4j;

import org.slf4j.spi.MDCAdapter;

import java.util.Map;

public class SmartLogMDCAdapter implements MDCAdapter {

    @Override
    public void put(String key, String val) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String get(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove(String key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map getCopyOfContextMap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setContextMap(Map contextMap) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
