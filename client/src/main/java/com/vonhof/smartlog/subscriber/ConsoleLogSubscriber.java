package com.vonhof.smartlog.subscriber;

import com.vonhof.smartlog.LogEntry;
import org.apache.commons.lang.StringUtils;


abstract public class ConsoleLogSubscriber implements LoggerSubscriber {

    protected String getMessage(String author, LogEntry logEntry) {
        String message = logEntry.getFormattedMessage();
        if (author == null || author.isEmpty()) {
            author = "unknown";
        }

        StackTraceElement[] trace = logEntry.getTrace();
        String location  = "";

        if (trace != null && trace.length > 0) {
            location = "|" + trace[0].getClassName() + ":" + trace[0].getLineNumber();
        }

        message = "{" + author + location + "} " + message;

        if (logEntry.getTags() != null && logEntry.getTags().length > 0) {
            message = String.format("[%s] %s", StringUtils.join(logEntry.getTags(), ","),message);
        }
        return message;
    }
}
