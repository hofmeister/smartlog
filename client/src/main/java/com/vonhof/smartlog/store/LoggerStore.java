package com.vonhof.smartlog.store;

import com.vonhof.smartlog.LogEntry;

public interface LoggerStore {
    public void write(String author, LogEntry logEntry);
}
