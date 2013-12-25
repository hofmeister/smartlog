package com.vonhof.smartlog.vcs;

import java.io.File;
import java.io.IOException;

public interface VCSRepository {

    void init(File file) throws IOException;

    AuthorMap resolveAuthor(File file) throws Exception;
    String resolveAuthor(File file, int lineNum) throws Exception;
}
