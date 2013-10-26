package com.vonhof.smartlog.impl;

import com.vonhof.smartlog.LogEntry;
import com.vonhof.smartlog.LoggerSubscriber;
import org.codehaus.plexus.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.LogRecord;
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

        String message = logEntry.getFormattedMessage();
        if (author != null && !author.isEmpty()) {
            message += " by " + author;
        }

        if (logEntry.getTags() != null && logEntry.getTags().length > 0) {
            message = String.format("[%s] %s", StringUtils.join(logEntry.getTags(),","),message);
        }

        LogRecord logRecord = new LogRecord(loggingLevel, message);
        logRecord.setSourceClassName(logEntry.getTrace()[0].getClassName());
        logRecord.setSourceMethodName(logEntry.getTrace()[0].getMethodName());
        logRecord.setParameters(logEntry.getArgs());
        logRecord.setThrown(logEntry.getThrowable());

        logger.log(logRecord);
    }
}
