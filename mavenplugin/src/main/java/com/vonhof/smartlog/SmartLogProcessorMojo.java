package com.vonhof.smartlog;


import org.apache.commons.io.FileUtils;
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

    private AuthorRegistryClassWriter classWriter = new AuthorRegistryClassWriter();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<String> compileSourceRoots = project.getCompileSourceRoots();

        final AuthorResolver authorResolver = getAuthorResolver();

        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        for(String sourceRoot : compileSourceRoots) {
            try {
                resolveDir(new File(sourceRoot), authorResolver);
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to scan directory",e);
            }
        }

        writeRegistryClass();
    }

    private void writeRegistryClass() throws MojoExecutionException {
        String classDef = classWriter.toString();

        File file = new File(targetDir + "/" + classWriter.toFileName());
        try {
            FileUtils.write(file, classDef);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write to file: " + file,e);
        }
    }

    private AuthorResolver getAuthorResolver() throws MojoExecutionException {
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

        return new GitAuthorResolver(repository);
    }

    private void resolveDir(File rootDir, AuthorResolver authorResolver) throws Exception {
        resolveDir(rootDir, rootDir, authorResolver);
    }

    private void resolveDir(File rootDir, File dir,  AuthorResolver authorResolver) throws Exception {
        String basePath = rootDir.getAbsolutePath();

        for(File file : dir.listFiles()) {
            if (file.isDirectory()) {
                resolveDir(rootDir, file, authorResolver);
            } else {
                String relPath = file.getAbsolutePath().substring(basePath.length()+1);
                AuthorMap authorMap = authorResolver.resolveAuthor(file,relPath);
                getLog().info(String.format("Resolved authors for file: " + relPath));
                classWriter.add(authorMap);
            }
        }
    }
}
