package com.vonhof.smartlog;


import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AuthorRegistryClassWriter {

    private String className = "SmartLogAuthorRegistry__";
    private String packageName = "com.vonhof.smartlog";

    private final StringBuilder sb = new StringBuilder();


    public AuthorRegistryClassWriter() {
        startClass();
    }

    public void add(AuthorMap authorMap) {
        sb.append("authors.put(\"").append(authorMap.getClassName()).append("\",\n")
                .append("new String[] {\n\t\"")
                .append(StringUtils.join(authorMap.getAuthors(), "\",\n\t\""))
                .append("\"\n}")
                .append(");\n");
    }


    private void startClass() {
        sb.append("package ").append(packageName).append(";");
        addImport(HashMap.class.getName());
        addImport(Map.class.getName());

        sb.append("\npublic final class ").append(className).append(" {\n");

        sb.append("private static final HashMap<String,String[]> authors = new HashMap<String,String[]>();\n");
        sb.append("static {\n");
    }

    private void endClass() {
        sb.append("\n}"); //End static block;

        sb.append("public static String getAuthor(String className,int line) {\n");
        sb.append("if (!authors.containsKey(className)) {return null;}\n");
        sb.append("if (authors.get(className).length <= line) {return null;}\n");
        sb.append("return authors.get(className)[line];\n");
        sb.append("}\n");

        sb.append("\n}");
    }

    private void addImport(String className) {
        sb.append("import ").append(className).append(";\n");
    }
    @Override
    public String toString() {
        endClass();
        return sb.toString();
    }

    public String toFileName() {
        return packageName.replaceAll("\\.","/")+"/"+className+".java";
    }
}
