package com.vonhof.smartlogdemo;


import com.vonhof.smartlog.Logger;
import com.vonhof.smartlog.LoggerFactory;

public class Lib {

    private static final Logger log = LoggerFactory.getLogger(Lib.class,"lib","demo");

    public Lib() {

    }

    public void demo() {
        log.warn("This is a warning from lib: %s", Math.random());
    }

}
