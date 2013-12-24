package com.vonhof.smartlog;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import java.io.File;


public class GitAuthorResolver implements AuthorResolver {

    private final Git git;
    private final String currentUser;
    private final String basePath;

    public GitAuthorResolver(Repository repository) {
        git = new Git(repository);
        currentUser = git.getRepository().getConfig().getString("user",null,"email");
        basePath = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
    }

    @Override
    public AuthorMap resolveAuthor(File file, String className) throws Exception {
        final AuthorMap authorMap = new AuthorMap(className, FileUtils.readLines(file).size());


        String filePath = file.getAbsolutePath();
        String relPath = filePath.substring(basePath.length() + 1);

        BlameResult result = git.blame().setFilePath(relPath).call();
        if (result != null) {
            result.computeAll();
        }

        for(int line = 0;line < authorMap.getAuthors().length;line++) {
            String author = "";
            try {
                if (result != null && result.hasSourceData(line)) {
                    PersonIdent sourceAuthor = result.getSourceAuthor(line);
                    author = sourceAuthor.getEmailAddress();
                    if (StringUtils.isEmpty(author)) {
                        author = sourceAuthor.getName();
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                //Ignore
            }

            if (StringUtils.isEmpty(author)) {
                //Uncomitted lines - fall back to config user
                author = currentUser;
            }

            authorMap.put(line, author);
        }


        return authorMap;
    }
}
