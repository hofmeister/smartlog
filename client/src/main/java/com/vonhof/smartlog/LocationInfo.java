package com.vonhof.smartlog;


public class LocationInfo {
    private final String className;
    private final int lineNumber;
    private final String fileName;

    public LocationInfo(String className, int lineNumber, String fileName) {
        this.className = className;
        this.lineNumber = lineNumber;
        this.fileName = fileName;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFileName() {
        return fileName;
    }
}
