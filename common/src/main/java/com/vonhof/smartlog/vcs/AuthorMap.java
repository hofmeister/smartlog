package com.vonhof.smartlog.vcs;


import java.io.File;
import java.io.IOException;

/**
 * Contains list of authors for a given file
 */
public class AuthorMap {
    /**
     * Array of authors - 1 per line - zero-based
     * e.g. If Line 1 = Henrik Hofmeister then authors[0] == "Henrik Hofmeister"
     */
    private final String[] authors;

    /**
     * The file in question
     */
    private final File file;

    public AuthorMap(File file, int lineCount) throws IOException {
        this.file = file;
        authors = new String[lineCount];
    }

    /**
     * Adds author to line
     * @param line
     * @param author
     */
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
