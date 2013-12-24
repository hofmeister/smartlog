package com.vonhof.smartlog;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
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

    private MavenProject project;
    private File targetDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project = (MavenProject) getPluginContext().get("project");
        if (project == null) {
            getLog().error("Could not find maven project: " + getPluginContext().get("project"));
            return;
        }

        targetDir = new File(project.getBuild().getDirectory() + "/smartlog");

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

        project.addCompileSourceRoot(targetDir.getPath());
    }

    private void writeRegistryClass(String classDef, File targetFile) throws MojoExecutionException {

        try {
            FileUtils.write(targetFile, classDef);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write to file: " + targetFile,e);
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
            } else if (file.getName().endsWith(".java") && !file.getName().startsWith("package-info")) {
                String className = file.getAbsolutePath().substring(basePath.length()+1).replaceAll("/",".");
                className = className.substring(0, className.length()-5); //Remove .java

                AuthorRegistryClassWriter classWriter = new AuthorRegistryClassWriter(className);
                File targetFile = new File(targetDir + "/" + classWriter.getFileName());

                AuthorMap authorMap = authorResolver.resolveAuthor(file,className);
                getLog().info("Resolved authors for class: " + className);

                writeRegistryClass(classWriter.buildClassString(authorMap), targetFile);
            }
        }
    }
}
