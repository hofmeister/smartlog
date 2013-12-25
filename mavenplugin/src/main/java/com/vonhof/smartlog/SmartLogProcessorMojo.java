package com.vonhof.smartlog;


import com.vonhof.smartlog.registry.AuthorRegistryWriter;
import com.vonhof.smartlog.vcs.AuthorMap;
import com.vonhof.smartlog.vcs.VCSFactory;
import com.vonhof.smartlog.vcs.VCSRepository;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This does some pre processing to all sources to enrich them with "blame" information.
 * Currently only GIT is supported
 * @goal preprocess
 */
@Mojo(  name = "process",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        threadSafe = true)
public class SmartLogProcessorMojo extends AbstractMojo {
    private final ExecutorService pool = Executors.newFixedThreadPool(100);

    private MavenProject project;
    private VCSRepository repository;
    private File targetDir;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project = (MavenProject) getPluginContext().get("project");
        if (project == null) {
            getLog().error("Could not find maven project: " + getPluginContext().get("project"));
            return;
        }

        targetDir = new File(project.getBuild().getDirectory() + "/smartlog");

        repository = VCSFactory.getRepository(project.getFile());

        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        final List<String> compileSourceRoots = project.getCompileSourceRoots();

        for(String sourceRoot : compileSourceRoots) {
            try {
                resolveDir(new File(sourceRoot));
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to scan directory",e);
            }
        }

        pool.shutdown();
        try {
            pool.awaitTermination(15, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            getLog().error("Author resolving aborted by user", e);
            return;
        }

        project.addCompileSourceRoot(targetDir.getPath());
    }

    private void resolveDir(File rootDir) throws Exception {
        resolveDir(rootDir, rootDir);
    }

    private void resolveDir(File rootDir, File dir) throws Exception {
        String basePath = rootDir.getAbsolutePath();

        for(File file : dir.listFiles()) {
            if (file.isDirectory()) {
                resolveDir(rootDir, file);
            } else if (file.getName().endsWith(".java") && !file.getName().startsWith("package-info")) {


                String className = file.getAbsolutePath().substring(basePath.length()+1).replaceAll("/", ".");
                className = className.substring(0, className.length()-5); //Remove .java

                pool.execute(new RegistryClassWriter(className, file));
            }
        }
    }


    private class RegistryClassWriter implements Runnable {

        private final String className;
        private final File file;

        private RegistryClassWriter(String className, File file) {
            this.className = className;
            this.file = file;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            AuthorRegistryWriter classWriter = new AuthorRegistryWriter(className);
            File targetFile = new File(targetDir + "/" + classWriter.getFileName());

            AuthorMap authorMap = null;
            try {
                authorMap = repository.resolveAuthor(file);
            } catch (Exception e) {
                getLog().warn("Could not resolve authors for file: " + file, e);
                return;
            }

            getLog().info("Resolved authors for class: " + className + " in " + (System.currentTimeMillis()-startTime) + "ms");

            try {
                FileUtils.write(targetFile, classWriter.buildClassString(authorMap));
            } catch (IOException e) {
                getLog().warn("Could not write to file: " + targetFile, e);
            }


        }
    }
}
