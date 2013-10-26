package com.vonhof.smartlog;


import java.io.IOException;

public class AuthorMap {
    private final String file;
    private final String[] authors;

    public AuthorMap(String file, int lineCount) throws IOException {
        this.file = file;
        authors = new String[lineCount];
    }

    public String getFile() {
        return file;
    }

    public void put(int line, String author) {
        authors[line] = author;
    }

    public String[] getAuthors() {
        return authors;
    }
}
