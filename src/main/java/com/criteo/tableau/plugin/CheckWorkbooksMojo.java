package com.criteo.tableau.plugin;

import com.criteo.tableau.validation.ConnectionsValidClass;
import com.criteo.tableau.validation.DashboardsHaveFixedSize$;
import com.criteo.tableau.validation.Failure;
import com.criteo.tableau.validation.MaxFileSize;
import com.criteo.tableau.validation.MaximumDashboards;
import com.criteo.tableau.validation.MaximumQuickFiltersPerDashboard;
import com.criteo.tableau.validation.MaximumSheetsPerDashboard;
import com.criteo.tableau.validation.NoExtract$;
import com.criteo.tableau.validation.NoUnusedSheet$;
import com.criteo.tableau.validation.NoVisibleSheet$;
import com.criteo.tableau.validation.ValidationRule;
import com.criteo.tableau.validation.ValidationStatus;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import scala.xml.Elem;
import scala.xml.XML;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mojo(name = "check-workbooks")
public class CheckWorkbooksMojo extends AbstractTableauMojo {

    @Parameter
    List<String> skippedRules = Collections.emptyList();

    @Parameter
    int maxDashboards = 3;

    @Parameter
    long maxFileSize = 3 * 1024 * 1024;

    @Parameter
    String[] validConnectionClasses = new String[]{"vertica"};

    @Parameter
    int maximumQuickFiltersPerDashboard = 3;

    @Parameter
    int maximumSheetsPerDashboard = 5;

    private <T> List<ValidationRule<T>> filterRules(List<ValidationRule<T>> rules) {
        List<ValidationRule<T>> newRules = new ArrayList<>();
        for (ValidationRule<T> rule : rules) {
            String[] classNameSplit = rule.getClass().getSimpleName().split("\\$");
            if (!skippedRules.contains(classNameSplit[classNameSplit.length - 1])) {
                newRules.add(rule);
            }
        }
        return newRules;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        List<ValidationRule<File>> fileRules =
                filterRules(Collections.<ValidationRule<File>>singletonList(new MaxFileSize(maxFileSize)));

        List<ValidationRule<Elem>> xmlRules = filterRules(Arrays.<ValidationRule<Elem>>asList(
                        new MaximumDashboards(maxDashboards),
                        NoExtract$.MODULE$,
                        new ConnectionsValidClass(validConnectionClasses),
                        DashboardsHaveFixedSize$.MODULE$,
                        NoVisibleSheet$.MODULE$,
                        NoUnusedSheet$.MODULE$,
                        new MaximumQuickFiltersPerDashboard(maximumQuickFiltersPerDashboard),
                        new MaximumSheetsPerDashboard(maximumSheetsPerDashboard))
        );

        int errorCount = 0;

        for (File file : getWorkbooks()) {
            List<Failure> failures = new ArrayList<>();

            for (ValidationRule<File> rule : fileRules) {
                ValidationStatus status = rule.validate(file);
                if (!status.isSuccess()) {
                    failures.add((Failure) status);
                }
            }

            if (failures.isEmpty()) {
                Elem xml = (Elem) XML.loadFile(file);
                for (ValidationRule<Elem> rule : xmlRules) {
                    ValidationStatus status = rule.validate(xml);
                    if (!status.isSuccess()) {
                        failures.add((Failure) status);
                    }
                }
            }

            if (!failures.isEmpty()) {
                errorCount++;
                getLog().error("");
                getLog().error(file.getPath() + ":");
                for (Failure failure : failures) {
                    getLog().error("- " + failure.msg());
                }
            }
        }

        if (errorCount > 0) {
            getLog().error("");
            throw new MojoFailureException(errorCount + " workbooks do not pass the validation rules.");
        }
    }
}
