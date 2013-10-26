package com.vonhof.smartlog;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import java.io.File;


public class GitAuthorResolver implements AuthorResolver {

    private final Git git;

    public GitAuthorResolver(Repository repository) {
        git = new Git(repository);
    }

    @Override
    public AuthorMap resolveAuthor(File file) throws Exception {
        final AuthorMap authorMap = new AuthorMap(file);

        String basePath = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
        String filePath = file.getAbsolutePath();
        String relPath = filePath.substring(basePath.length()+1);

        System.out.println("Reading blame for path: " + relPath + " | " + basePath);

        BlameResult result = git.blame().setFilePath(relPath).call();
        int idx;
        while((idx = result.computeNext()) > -1) {
            for(;idx < result.lastLength();idx++) {
                PersonIdent sourceAuthor = result.getSourceAuthor(idx);
                String author = sourceAuthor.getEmailAddress();
                authorMap.put(idx, author);
            }
        }

        return authorMap;
    }
}
