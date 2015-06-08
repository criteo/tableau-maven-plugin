package com.criteo.tableau.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public abstract class AbstractTableauMojo extends AbstractMojo {

    @Parameter
    File workbookSourcePath = new File("workbooks");

    public Collection<File> getWorkbooks() {
        return FileUtils.listFiles(workbookSourcePath, new String[]{"twb"}, true);
    }

    public String readFromFile(File file) throws MojoExecutionException {
        try {
            return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read from "+ file, e);
        }
    }

    public void writeToFile(File file, String str) throws MojoExecutionException {
        try {
            Files.write(Paths.get(file.getAbsolutePath()), str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write to "+ file, e);
        }
    }
}
