package com.vonhof.smartlog.vcs;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VCSFactory {

    private static Map<String,VCSRepository> cached = new ConcurrentHashMap<String, VCSRepository>();

    private static Class<VCSRepository>[] vcsRepoTypes = new Class[] {
        GitRepository.class
    };

    public static VCSRepository getRepository(File file) {
        if (cached.containsKey(file.getParent())) {
            return cached.get(file.getParent());
        }

        for(Class<VCSRepository> vcsRepoType : vcsRepoTypes) {
            VCSRepository repo = tryRepoType(vcsRepoType, file);
            if (repo != null) {
                cached.put(file.getParent(), repo);
                return repo;
            }
        }

        throw new RuntimeException("No version control system found for file path: " + file.getAbsolutePath());
    }

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
