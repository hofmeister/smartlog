package com.vonhof.smartlog.slf4j;

import com.vonhof.smartlog.Level;
import com.vonhof.smartlog.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.spi.LocationAwareLogger;

import java.io.Serializable;
import java.util.Arrays;

public class SmartLogLoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger, Serializable {

    private final Logger log;

    public SmartLogLoggerAdapter(Logger log) {
        this.log = log;
    }

    @Override
    public boolean isTraceEnabled() {
        return log.willLog(Level.TRACE);
    }

    @Override
    public void trace(String msg) {
        log.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        log.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log.trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.willLog(Level.DEBUG);
    }

    @Override
    public void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        log.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.willLog(Level.INFO);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        log.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        log.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.willLog(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        log.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log.warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.willLog(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        log.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    @Override
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {
        Object[] args = Arrays.copyOf(argArray, argArray.length + 1);
        args[argArray.length] = t;

        switch (level) {
            case TRACE_INT:
                trace(message, args);
                break;
            case DEBUG_INT:
                debug(message, args);
                break;
            case INFO_INT:
                info(message, args);
                break;
            case WARN_INT:
                warn(message, args);
                break;
            case ERROR_INT:
                error(message, args);
                break;
        }

    }
}
