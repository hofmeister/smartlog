package com.vonhof.smartlog;


import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class AuthorRegistryClassWriter {
    private static final String CLASS_POSTFIX = "__SLAUTHORS";

    private final String className;
    private final String packageName;

    private final StringBuilder sb = new StringBuilder();


    public AuthorRegistryClassWriter(AuthorMap authorMap) {
        String[] classParts = authorMap.getClassName().split("\\.");

        className = classParts[classParts.length-1] + CLASS_POSTFIX;
        packageName = StringUtils.join(Arrays.copyOfRange(classParts,0,classParts.length-1),".");

        buildClassString(authorMap);
    }


    private void buildClassString(AuthorMap authorMap) {
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
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    public String getFileName() {
        return packageName.replaceAll("\\.","/")+"/"+className+".java";
    }
}
