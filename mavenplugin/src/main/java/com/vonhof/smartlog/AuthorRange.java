package com.vonhof.smartlog;


public class AuthorRange {
    private final String author;
    private final int firstLine;
    private final int lastLine;

    public AuthorRange(String author, int firstLine, int lastLine) {
        this.author = author;
        this.firstLine = firstLine;
        this.lastLine = lastLine;
    }


    public String getAuthor() {
        return author;
    }

    public int getFirstLine() {
        return firstLine;
    }

    public int getLastLine() {
        return lastLine;
    }
}
