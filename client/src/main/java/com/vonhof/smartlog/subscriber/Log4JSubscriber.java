package com.vonhof.smartlog.subscriber;


import com.vonhof.smartlog.LogEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Log4JSubscriber extends ConsoleLogSubscriber {

    @Override
    public void logged(String author, LogEntry logEntry) {
        Logger log4jLogger = Logger.getLogger(logEntry.getClz());

        Level log4jLevel = null;
        switch (logEntry.getLevel()) {
            case TRACE:
                log4jLevel = Level.TRACE;
                break;
            case DEBUG:
                log4jLevel = Level.DEBUG;
                break;
            case INFO:
                log4jLevel = Level.INFO;
                break;
            case WARN:
                log4jLevel = Level.WARN;
                break;
            case ERROR:
                log4jLevel = Level.ERROR;
                break;
            case FATAL:
                log4jLevel = Level.FATAL;
                break;
        }

        if (!log4jLogger.isEnabledFor(log4jLevel)) {
            return;
        }

        log4jLogger.log(
            logEntry.getClz().getName(),
            log4jLevel,
            getMessage(author, logEntry),
            logEntry.getThrowable());
    }
}
