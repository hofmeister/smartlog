package com.vonhof.smartlog;


import java.io.File;

public interface AuthorResolver {
    AuthorMap resolveAuthor(File file, String className) throws Exception;
}
