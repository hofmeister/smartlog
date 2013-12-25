package com.vonhof.smartlog;


public class Logger {
    private final String[] tags;
    private final Class clz;
    private final SmartLogInstance smartLog;

    public Logger(Class clz, String ... tags) {
        this(SmartLog.getInstance(), clz, tags);
    }

    public Logger(SmartLogInstance smartLog, Class clz, String ... tags) {
        this.tags = tags;
        this.clz = clz;
        this.smartLog = smartLog;
    }

    public static Logger getLogger(Class clz, String ... tags) {
        return new Logger(clz,tags);
    }

    public boolean willLog(Level lvl) {
        Level logLevel = smartLog.getLogLevel(clz, tags);
        return logLevel.valid(lvl);
    }

    public boolean willLog(Level lvl, String ... tags) {
        Level logLevel = smartLog.getLogLevel(clz, tags);
        return logLevel.valid(lvl);
    }

    public Logger tags(String ... tags) {
        return new Logger(clz, tags);
    }

    public void log(Level lvl, String msg, Object ... args) {
        if (!willLog(lvl, tags)) {
            return;
        }
        smartLog.write(lvl, clz, tags, msg, args);
    }

    public void trace(String msg, Object ... args) {
        log(Level.TRACE, msg, args);
    }

    public void debug(String msg, Object ... args) {
        log(Level.DEBUG, msg, args);
    }

    public void info(String msg, Object ... args) {
        log(Level.INFO, msg, args);
    }

    public void warn(String msg, Object ... args) {
        log(Level.WARN, msg, args);
    }

    public void error(String msg, Object ... args) {
        log(Level.ERROR, msg, args);
    }

    public void fatal(String msg, Object ... args) {
        log(Level.FATAL, msg, args);
    }

    public void trace(Throwable ex) {
        trace(null, ex);
    }

    public void debug(Throwable ex) {
        debug(null, ex);
    }

    public void info(Throwable ex) {
        info(null, ex);
    }

    public void warn(Throwable ex) {
        warn(null, ex);
    }

    public void error(Throwable ex) {
        error(null, ex);
    }

    public void fatal(Throwable ex) {
        fatal(null, ex);
    }
}


