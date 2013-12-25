package com.vonhof.smartlog.vcs;


import java.io.File;
import java.io.IOException;

public class AuthorMap {
    private final String[] authors;
    private final File file;

    public AuthorMap(File file, int lineCount) throws IOException {
        this.file = file;
        authors = new String[lineCount];
    }

    public void put(int line, String author) {
        authors[line] = author;
    }

    public String[] getAuthors() {
        return authors;
    }

    public File getFile() {
        return file;
    }
}
