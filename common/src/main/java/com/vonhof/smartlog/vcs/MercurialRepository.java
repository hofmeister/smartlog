package com.vonhof.smartlog.vcs;

import com.aragost.javahg.BaseRepository;
import com.aragost.javahg.Repository;
import com.aragost.javahg.commands.AnnotateCommand;
import com.aragost.javahg.commands.AnnotateLine;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MercurialRepository implements VCSRepository {
    private BaseRepository repo;
    private String defaultUser = null; //Not sure how to get this for mercurial

    @Override
    public void init(File file) throws IOException {
        repo = Repository.open(file);

    }

    @Override
    public AuthorMap resolveAuthor(File file) throws Exception {
        String relativePath = getRelativePath(file);
        List<AnnotateLine> annotatedLines = AnnotateCommand.on(repo).execute(relativePath);

        AuthorMap out = new AuthorMap(file, annotatedLines.size());
        int i = 0;
        for(AnnotateLine line: annotatedLines) {
            out.put(i, line.getChangeset().getUser());
            i++;
        }

        return out;
    }

    private String getRelativePath(File file) {
        String filePath = file.getAbsolutePath();
        return filePath.substring(repo.getDirectory().getAbsolutePath().length() + 1);
    }

    @Override
    public String resolveAuthor(File file, int lineNum) throws Exception {
        String[] authors = resolveAuthor(file).getAuthors();
        if (authors.length <= lineNum) {
            return defaultUser;
        }
        return authors[lineNum];
    }
}
