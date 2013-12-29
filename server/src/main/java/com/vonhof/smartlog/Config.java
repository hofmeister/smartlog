package com.vonhof.smartlog;

public class Config {

    public static int getServerPort() {
        return Integer.valueOf(System.getProperty("smartlog.port","8844"));
    }

    public static String getESHost() {
        return System.getProperty("es.host","localhost");
    }

    public static int getESPort() {
        return Integer.valueOf(System.getProperty("es.port","9301"));
    }
}
