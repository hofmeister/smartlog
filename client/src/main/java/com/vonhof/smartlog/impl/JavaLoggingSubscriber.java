package com.vonhof.smartlog.impl;

import com.vonhof.smartlog.LogEntry;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JavaLoggingSubscriber extends ConsoleLogSubscriber {
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


        LogRecord logRecord = new LogRecord(loggingLevel, getMessage(author,logEntry));
        logRecord.setSourceClassName(logEntry.getTrace()[0].getClassName());
        logRecord.setSourceMethodName(logEntry.getTrace()[0].getMethodName());
        logRecord.setParameters(logEntry.getArgs());
        logRecord.setThrown(logEntry.getThrowable());

        logger.log(logRecord);
    }
}
