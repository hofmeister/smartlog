package com.vonhof.smartlog;


import java.io.IOException;

public class AuthorMap {
    private final String className;
    private final String[] authors;

    public AuthorMap(String className, int lineCount) throws IOException {
        this.className = className;
        authors = new String[lineCount];
    }

    public String getClassName() {
        return className;
    }

    public void put(int line, String author) {
        authors[line] = author;
    }

    public String[] getAuthors() {
        return authors;
    }
}
