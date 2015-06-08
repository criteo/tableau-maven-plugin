package com.criteo.tableau.plugin;

import com.criteo.tableau.templating.XmlAttributeReplacer;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Mojo(name = "untemplate-workbooks")
public class UntemplateWorkbooksMojo extends AbstractTableauMojo {

    @Parameter(required = true)
    Map<String, String> replacementMap;

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (File file : getWorkbooks()) {
            getLog().info("Untemplating " + file.getName());
            String xmlDocument = readFromFile(file);

            for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
                xmlDocument = xmlDocument.replaceAll("%%" + entry.getKey().toUpperCase() + "%%", entry.getValue());
            }

            writeToFile(file, xmlDocument);
        }
    }
}
