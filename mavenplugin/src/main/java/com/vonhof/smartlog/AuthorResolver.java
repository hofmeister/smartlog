package com.vonhof.smartlog;


import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public interface AuthorResolver {
    AuthorMap resolveAuthor(File file) throws GitAPIException, Exception;
}
