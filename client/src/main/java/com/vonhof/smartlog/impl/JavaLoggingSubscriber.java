package com.vonhof.smartlog.impl;

import com.vonhof.smartlog.LogEntry;
import com.vonhof.smartlog.LoggerSubscriber;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLoggingSubscriber implements LoggerSubscriber {
    @Override
    public void logged(String author, LogEntry logEntry) {
        Logger logger = Logger.getLogger(logEntry.getClz().getName());

        Level loggingLevel = null;
        switch (logEntry.getLevel()) {
            case TRACE:
                loggingLevel = Level.FINEST;
                break;
            case DEBUG:
                loggingLevel = Level.FINE;
                break;
            case INFO:
                loggingLevel = Level.INFO;
                break;
            case WARN:
                loggingLevel = Level.WARNING;
                break;
            case ERROR:
            case FATAL:
                loggingLevel = Level.SEVERE;
                break;
        }

        if (!logger.isLoggable(loggingLevel)) {
            return;
        }

        if (logEntry.getThrowable() != null) {
            logger.log(loggingLevel, logEntry.getFormattedMessage(), logEntry.getThrowable());
        } else {
            logger.log(loggingLevel, logEntry.getFormattedMessage());
        }
    }
}
