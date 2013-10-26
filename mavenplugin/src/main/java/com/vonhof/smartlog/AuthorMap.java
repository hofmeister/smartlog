package com.vonhof.smartlog;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class AuthorMap {
    private final File file;
    private final String[] authors;

    public AuthorMap(File file) throws IOException {
        this.file = file;
        int lineCount = FileUtils.readLines(file).size();
        authors = new String[lineCount];
    }

    public File getFile() {
        return file;
    }

    public void put(int line, String author) {
        authors[line] = author;
    }
}
