package com.vonhof.smartlog.store;

public class LogEntryDTO {
    private String message;
    private String[] args;
    private String clazz;
    private String[] tags;
    private StackTraceDTO[] trace;
    private String author;
    private String project;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public StackTraceDTO[] getTrace() {
        return trace;
    }

    public void setTrace(StackTraceDTO[] trace) {
        this.trace = trace;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
