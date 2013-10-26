package com.vonhof.smartlog;

public interface LoggerStore {
    public void write(String author, LogEntry logEntry);
}
