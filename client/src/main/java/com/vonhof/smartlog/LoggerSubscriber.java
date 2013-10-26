package com.vonhof.smartlog;

public interface LoggerSubscriber {

    public void logged(String author, LogEntry logEntry);
}
