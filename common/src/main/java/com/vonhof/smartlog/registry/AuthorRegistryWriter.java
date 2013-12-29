package com.vonhof.smartlog.registry;

import com.vonhof.smartlog.vcs.AuthorMap;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Writes author registry to disk for a given classname and AuthorMap.
 *
 * Currently it creates a public final class which has a single private static property that is an array of authors
 * (1 per line). It conserves memory by only writing a string name every time it changes, and otherwise writes null's.
 *
 * To read these registries see {@link AuthorRegistryReader}
 */
public class AuthorRegistryWriter {
    private static final String CLASS_POSTFIX = "__SLAUTHORS";

    private final String className;
    private final String packageName;


    public AuthorRegistryWriter(String className) {
        String[] classParts = className.split("\\.");

        this.className = classParts[classParts.length-1] + CLASS_POSTFIX;
        this.packageName = StringUtils.join(Arrays.copyOfRange(classParts, 0, classParts.length - 1), ".");
    }

    public String buildClassString(AuthorMap authorMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";");

        sb.append("\npublic final class ").append(className).append(" {\n");

        sb.append("private static String[] authors = new String[] {\n\t");

        String last = null;
        for(int i = 0; i < authorMap.getAuthors().length; i++) {
            String author = authorMap.getAuthors()[i];
            if (i > 0) {
                sb.append(",");
            }
            if (i == 0 || !last.equals(author)) {
                sb.append("\"").append(author).append("\"");
            } else {
                sb.append("null");
            }

            last = author;
        }

        sb.append("\n};");

        sb.append("\n}");

        return sb.toString();
    }


    public String getFileName() {
        return packageName.replaceAll("\\.","/")+"/"+className+".java";
    }
}
