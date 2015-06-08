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

@Mojo(name = "template-workbooks")
public class TemplateWorkbooksMojo extends AbstractTableauMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (File file : getWorkbooks()) {
            getLog().info("Templating " + file.getName());
            String xmlDocument = readFromFile(file);
            xmlDocument = XmlAttributeReplacer.connectionReplacer().replace(xmlDocument);
            writeToFile(file, xmlDocument);
        }
    }
}
