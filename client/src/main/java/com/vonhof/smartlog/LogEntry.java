package com.vonhof.smartlog;


import java.util.Date;

public class LogEntry {
    private final Date created = new Date();
    private final Level level;
    private final Class clz;
    private final String[] tags;
    private final String msg;
    private final Object[] args;
    private final Throwable throwable;
    private final StackTraceElement[] trace;

    public LogEntry(Level level, Class clz, String[] tags, String msg, Object[] args, Throwable throwable) {
        this.level = level;
        this.clz = clz;
        this.tags = tags;
        this.msg = msg;
        this.args = args;
        this.throwable = throwable;
        this.trace = throwable.getStackTrace();
    }

    public LogEntry(Level level, Class clz, String[] tags, String msg, Object[] args, StackTraceElement[] trace) {
        this.level = level;
        this.clz = clz;
        this.tags = tags;
        this.msg = msg;
        this.args = args;
        this.throwable = null;
        this.trace = trace;
    }

    public Date getCreated() {
        return created;
    }

    public Level getLevel() {
        return level;
    }

    public Class getClz() {
        return clz;
    }

    public String[] getTags() {
        return tags;
    }

    public String getMsg() {
        return msg;
    }

    public Object[] getArgs() {
        return args;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public StackTraceElement[] getTrace() {
        return trace;
    }

    public String getFormattedMessage() {
        if (msg == null) {
            return null;
        }
        return String.format(msg, args);
    }
}
