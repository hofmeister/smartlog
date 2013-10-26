package com.vonhof.smartlog;


public enum Level {
    TRACE(5),
    DEBUG(4),
    INFO(3),
    WARN(2),
    ERROR(1),
    FATAL(0);

    private final int level;

    private Level(int level) {
        this.level = level;
    }

    public boolean valid(Level lvl) {
        return level >= lvl.level;
    }
}
