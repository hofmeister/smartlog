package com.vonhof.smartlog.log4j;


import com.vonhof.smartlog.LocationInfo;
import com.vonhof.smartlog.LoggerFactory;
import com.vonhof.smartlog.store.SmartLogServerStore;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class SmartLogAppender extends AppenderSkeleton {

    public SmartLogAppender() {
        LoggerFactory.setStore(new SmartLogServerStore());
    }

    @Override
    protected void append(LoggingEvent event) {

        try {
            doSmartLog(event);
        } catch (Exception e) {
            java.util.logging.Logger
                    .getLogger(SmartLogAppender.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Failed to append to smart log:" + event.getLocationInformation(), e);
        }
    }

    private void doSmartLog(LoggingEvent event) throws ClassNotFoundException {
        org.apache.log4j.spi.LocationInfo locationInformation = event.getLocationInformation();
        LocationInfo location = new LocationInfo(locationInformation.getClassName(),
                Integer.valueOf(locationInformation.getLineNumber()),
                locationInformation.getFileName());

        Class<?> loggerClass = Class.forName(location.getClassName());

        com.vonhof.smartlog.Level smartLevel = com.vonhof.smartlog.Level.INFO;

        switch (event.getLevel().toInt()) {
            case Level.FATAL_INT:
                smartLevel = com.vonhof.smartlog.Level.FATAL;
                break;
            case Level.ERROR_INT:
                smartLevel = com.vonhof.smartlog.Level.ERROR;
                break;
            case Level.WARN_INT:
                smartLevel = com.vonhof.smartlog.Level.WARN;
                break;
            case Level.INFO_INT:
                smartLevel = com.vonhof.smartlog.Level.INFO;
                break;
            case Level.DEBUG_INT:
                smartLevel = com.vonhof.smartlog.Level.DEBUG;
                break;
            case Level.TRACE_INT:
                smartLevel = com.vonhof.smartlog.Level.TRACE;
                break;
        }

        if (event.getThrowableInformation() != null) {
            LoggerFactory.write(smartLevel, loggerClass,
                    new String[0],event.getRenderedMessage(),event.getThrowableInformation().getThrowable());
        } else {
            LoggerFactory.write(smartLevel, loggerClass,
                    new String[0], event.getRenderedMessage(), location);
        }

    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
