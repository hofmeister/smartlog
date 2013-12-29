package com.vonhof.smartlog.registry;

import java.lang.reflect.Field;

/**
 * Reads written author registries for a particular class.
 *
 * The format is described in {@link AuthorRegistryWriter}
 */
public class AuthorRegistryReader {
    private final Class target;
    private String[] authorList;

    public AuthorRegistryReader(Class target) {
        this.target = target;
        readRegistryForTarget();
    }

    private void readRegistryForTarget() {
        Class<?> registryClass = null;
        try {
            registryClass = target.getClassLoader().loadClass(target.getName() + "__SLAUTHORS");

            Field authors = registryClass.getDeclaredField("authors");
            authors.setAccessible(true);

            authorList = (String[]) authors.get(null);
        } catch (Exception e) {
            //Ignore
        } finally {
            if (authorList == null) {
                authorList = new String[0];
            }
        }

    }

    public String getAuthor(int line) {
        if (authorList.length <= line) {
            return null;
        }

        for (int i = line; i > -1; i--) {
            if (authorList[i] != null)
                return authorList[i];
        }

        return null;
    }
}
