package com.vonhof.smartlog.subscriber;

import com.vonhof.smartlog.LogEntry;
import org.apache.commons.lang.StringUtils;


abstract public class ConsoleLogSubscriber implements LoggerSubscriber {

    protected String getMessage(String author, LogEntry logEntry) {
        String message = logEntry.getFormattedMessage();
        if (author != null && !author.isEmpty()) {
            message = "{"+author+"} " + message;
        }

        if (logEntry.getTags() != null && logEntry.getTags().length > 0) {
            message = String.format("[%s] %s", StringUtils.join(logEntry.getTags(), ","),message);
        }
        return message;
    }
}
