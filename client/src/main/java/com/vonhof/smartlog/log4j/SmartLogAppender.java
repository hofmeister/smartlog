package com.vonhof.smartlog.log4j;


import com.vonhof.smartlog.LocationInfo;
import com.vonhof.smartlog.SmartLogInstance;
import com.vonhof.smartlog.subscriber.SystemOutSubscriber;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;

public class SmartLogAppender extends AppenderSkeleton implements AppenderAttachable {

    private final SmartLogInstance smartLog;
    private AppenderAttachableImpl aai = new AppenderAttachableImpl();

    public SmartLogAppender() {
        smartLog = new SmartLogInstance();
        smartLog.addSubscriber( new SystemOutSubscriber() );
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

        aai.appendLoopOnAppenders(event);
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
        String author;

        if (event.getThrowableInformation() != null) {
            author = smartLog.write(smartLevel, loggerClass,
                    new String[0], event.getRenderedMessage(), event.getThrowableInformation().getThrowable());
        } else {
            author = smartLog.write(smartLevel, loggerClass,
                    new String[0], event.getRenderedMessage(), location);
        }

        if (author != null) {
            MDC.put("_author", author);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public void addAppender(Appender appender) {
        aai.addAppender(appender);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<Object> getAllAppenders() {
        return aai.getAllAppenders();
    }

    public Appender getAppender(String s) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender appender) {
        return aai.isAttached(appender);
    }

    public void removeAllAppenders() {
        aai.removeAllAppenders();
    }

    public void removeAppender(Appender appender) {
        aai.removeAppender(appender);
    }

    public void removeAppender(String s) {
        aai.removeAppender(s);
    }
}
