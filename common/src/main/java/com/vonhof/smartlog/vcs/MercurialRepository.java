package com.vonhof.smartlog.vcs;

import com.aragost.javahg.BaseRepository;
import com.aragost.javahg.Repository;
import com.aragost.javahg.commands.AnnotateCommand;
import com.aragost.javahg.commands.AnnotateLine;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * EXPERIMENTAL: Mercurial implementation of the VCSRepository.
 *
 * Untested, most likely broken.
 */
public class MercurialRepository implements VCSRepository {
    private BaseRepository repo;
    private String defaultUser = null; //Not sure how to get this for mercurial

    /**
     * Init this repo - should only be called once, by the VCSFactory
     * @param file
     * @throws IOException
     */
    @Override
    public void init(File file) throws IOException {
        repo = Repository.open(file);
    }

    /**
     * Resolve authors for entire file
     * @param file
     * @return
     * @throws Exception
     */
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

    /**
     * Resolve author for a single line in the file
     *
     * Calls resolveAuthor(File file) so no performance benefit in calling this instead for mercurial.
     *
     * @param file
     * @param lineNum
     * @return
     * @throws Exception
     */
    @Override
    public String resolveAuthor(File file, int lineNum) throws Exception {
        String[] authors = resolveAuthor(file).getAuthors();
        if (authors.length <= lineNum) {
            return defaultUser;
        }
        return authors[lineNum];
    }

    /**
     * Get relative path of file
     * @param file
     * @return
     */
    private String getRelativePath(File file) {
        String filePath = file.getAbsolutePath();
        return filePath.substring(repo.getDirectory().getAbsolutePath().length() + 1);
    }
}
