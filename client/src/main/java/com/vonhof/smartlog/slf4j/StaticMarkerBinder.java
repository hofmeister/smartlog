package com.vonhof.smartlog.slf4j;

import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder {

    @Override
    public IMarkerFactory getMarkerFactory() {
        return null;
    }

    @Override
    public String getMarkerFactoryClassStr() {
        return null;
    }
}
