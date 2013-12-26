package com.vonhof.smartlog.vcs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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

        if (!file.exists()) {
            throw new IllegalStateException("Can not init git repo from non-existing file: " + file.getAbsolutePath());
        }


        FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();

        fileRepositoryBuilder
                .readEnvironment()
                .findGitDir(file.getAbsoluteFile());

        repository = fileRepositoryBuilder
                .setWorkTree(fileRepositoryBuilder.getGitDir().getParentFile())
                .build();

        if (!repository.getDirectory().exists()) {
            throw new IOException("Git root does not exist: " + repository.getDirectory());
        }

        git = new Git(repository);
        currentUser = git.getRepository().getConfig().getString("user",null,"email");
        basePath = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
    }

    @Override
    public AuthorMap resolveAuthor(File file) throws Exception {
        if (!file.exists()) {
            return new AuthorMap(file,0);
        }

        List<String> result = getAuthors(file);

        final AuthorMap authorMap = new AuthorMap(file, result.size());

        int idx = 0;
        for(String author : result) {
            authorMap.put(idx, author);
            idx++;
        }

        return authorMap;
    }

    /**
     * Resolve author
     * @param file
     * @param lineNum 1-based
     * @return
     * @throws Exception
     */
    @Override
    public String resolveAuthor(File file, int lineNum) throws Exception {
        if (!file.exists()) {
            return null;
        }

        List<String> result = getAuthors(file, lineNum);
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private List<String> getAuthors(File file) {
        return getAuthors(file, null);
    }

    private List<String> getAuthors(File file, Integer lineNum) {
        List<String> out = new LinkedList<String>();
        try {
            ProcessBuilder processBuilder;
            if (lineNum != null) {
                processBuilder = new ProcessBuilder(
                        "git","--no-pager","blame","-wec",
                        "-L",lineNum+","+lineNum,
                        getRelativePath(file));
            } else {
                processBuilder = new ProcessBuilder(
                        "git","--no-pager","blame","-wec",
                        getRelativePath(file));
            }

            processBuilder.directory(new File(basePath));
            Process p = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 2) {
                    continue;
                }
                String author = parts[1].trim().replaceAll("(^\\(<|>$)","");
                out.add(author);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    private String getRelativePath(File file) {
        String filePath = file.getAbsolutePath();
        return filePath.substring(basePath.length() + 1);
    }

}
