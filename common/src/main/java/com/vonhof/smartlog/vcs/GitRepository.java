package com.vonhof.smartlog.vcs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitRepository implements VCSRepository {

    private Repository repository;
    private Git git;
    private String currentUser;
    private String basePath;

    @Override
    public void init(File file) throws IOException {
        if (repository != null) {
            throw new IllegalStateException("Can not init git repo twice.");
        }

        repository = new FileRepositoryBuilder()
                .setWorkTree(file)
                .readEnvironment()
                .findGitDir(file).build();

        if (!repository.getDirectory().exists()) {
            throw new IOException("Git root does not exist: " + repository.getDirectory());
        }

        git = new Git(repository);
        currentUser = git.getRepository().getConfig().getString("user",null,"email");
        basePath = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
    }

    @Override
    public AuthorMap resolveAuthor(File file) throws Exception {
        final AuthorMap authorMap = new AuthorMap(file, FileUtils.readLines(file).size());

        BlameResult result = getBlameResult(file);
        if (result != null) {
            result.computeAll();
        }

        for(int line = 0;line < authorMap.getAuthors().length;line++) {
            String author = getAuthorFromLine(result, line);
            authorMap.put(line, author);
        }

        return authorMap;
    }

    @Override
    public String resolveAuthor(File file, int lineNum) throws Exception {

        BlameResult result = getBlameResult(file);
        if (result != null) {
            result.computeRange(lineNum, lineNum);
        }

        return getAuthorFromLine(result, lineNum);
    }

    private BlameResult getBlameResult(File file) throws GitAPIException {
        String relPath = getRelativePath(file);

        return git.blame().setFilePath(relPath).call();
    }

    private String getRelativePath(File file) {
        String filePath = file.getAbsolutePath();
        return filePath.substring(basePath.length() + 1);
    }

    private String getAuthorFromLine(BlameResult result, int line) {
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

        return author;
    }
}
