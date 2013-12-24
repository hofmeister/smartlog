package com.vonhof.smartlog;


import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class AuthorRegistryClassWriter {
    private static final String CLASS_POSTFIX = "__SLAUTHORS";

    private final String className;
    private final String packageName;


    public AuthorRegistryClassWriter(String className) {
        String[] classParts = className.split("\\.");

        this.className = classParts[classParts.length-1] + CLASS_POSTFIX;
        this.packageName = StringUtils.join(Arrays.copyOfRange(classParts,0,classParts.length-1),".");
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
