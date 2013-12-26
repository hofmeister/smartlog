package com.vonhof.smartlog.subscriber;

import com.vonhof.smartlog.LogEntry;

import java.util.Date;

public class SystemOutSubscriber extends ConsoleLogSubscriber {
    @Override
    public void logged(String author, LogEntry logEntry) {
        System.out.println(new Date().toString() + " : " + getMessage(author, logEntry));
    }
}
