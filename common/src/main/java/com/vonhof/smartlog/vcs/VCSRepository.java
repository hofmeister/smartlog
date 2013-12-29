package com.vonhof.smartlog.vcs;

import java.io.File;
import java.io.IOException;

public interface VCSRepository {

    /**
     * Inits the repo - this should only be called by the VCSFactory
     * @param file
     * @throws IOException
     */
    void init(File file) throws IOException;

    /**
     * Resolves authors for entire file
     * @param file
     * @return
     * @throws Exception
     */
    AuthorMap resolveAuthor(File file) throws Exception;

    /**
     * Resolve author for single line in file
     * @param file
     * @param lineNum
     * @return
     * @throws Exception
     */
    String resolveAuthor(File file, int lineNum) throws Exception;
}
