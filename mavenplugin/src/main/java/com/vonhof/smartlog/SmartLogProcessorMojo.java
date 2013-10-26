package com.vonhof.smartlog;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This does some pre processing to all sources to enrich them with "blame" information.
 * Currently only GIT is supported
 * @goal preprocess
 */
@Mojo(  name = "process",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        threadSafe = true)
public class SmartLogProcessorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true, readonly = true)
    private File targetDir;

    private AuthorResolver authorResolver;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> compileSourceRoots = project.getCompileSourceRoots();

        Repository repository;
        try {
            repository = new FileRepositoryBuilder()
                    .setWorkTree(project.getFile().getParentFile())
                    .readEnvironment()
                    .findGitDir(project.getFile()).build();

        } catch (IOException e) {
            throw new MojoExecutionException("Could not find GIT root",e);
        }

        if (!repository.getDirectory().exists()) {
            throw new MojoExecutionException("Git root does not exist: " + repository.getDirectory());
        }

        authorResolver = new GitAuthorResolver(repository);


        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        for(String sourceRoot : compileSourceRoots) {
            try {
                searchDir(new File(sourceRoot));
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to scan directory",e);
            }
        }
    }

    private void searchDir(File dir) throws Exception {

        File baseDir = project.getFile().getParentFile();

        for(File file : dir.listFiles()) {
            if (file.isDirectory()) {
                searchDir(file);
            } else {
                authorResolver.resolveAuthor(file);
            }
        }
    }
}
