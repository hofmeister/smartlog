package com.vonhof.smartlog.vcs;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main entry point for getting VCSRepository instance for a file
 */
public class VCSFactory {

    private static Map<String,VCSRepository> cached = new ConcurrentHashMap<String, VCSRepository>();

    /**
     * Array of VCSRepo implementation - which the factory will try (in order)
     */
    private static Class<VCSRepository>[] vcsRepoTypes = new Class[] {
        GitRepository.class,
        MercurialRepository.class
    };

    /**
     * Gets a repo implementation for a given file. Will try each until successful and throw a runtime exception
     * if no valid repo was found.
     * @param file
     * @return
     */
    public static VCSRepository getRepository(File file) {
        if (cached.containsKey(file.getParent())) {
            return cached.get(file.getParent());
        }

        for(Class<VCSRepository> vcsRepoType : vcsRepoTypes) {
            try {
                VCSRepository repo = tryRepoType(vcsRepoType, file);
                if (repo != null) {
                    cached.put(file.getParent(), repo);
                    return repo;
                }
            } catch (Throwable ex) {
                //Ignore
            }
        }

        throw new RuntimeException("No version control system found for file path: " + file.getAbsolutePath());
    }

    /**
     * Tries to use the given repo type for the file. Returns null if unsuccessful.
     * @param vcsRepoType
     * @param file
     * @return
     */
    private static VCSRepository tryRepoType(Class<VCSRepository> vcsRepoType, File file) {
        try {
            VCSRepository repo = vcsRepoType.newInstance();
            repo.init(file);
            return repo;
        } catch (Exception e) {
            //Ignore
        }

        return null;
    }
}
