package com.vonhof.smartlog.subscriber;

import com.vonhof.smartlog.LogEntry;

public interface LoggerSubscriber {

    public void logged(String author, LogEntry logEntry);
}
