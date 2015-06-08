package com.criteo.tableau.plugin;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class UntemplateWorkbooksMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testLookup() throws Exception {
        File pom = new File("src/test/resources/unit/test-project/pom.xml");

        Mojo mojo = mojoRule.lookupMojo("untemplate-workbooks", pom);
        assertNotNull(mojo);
    }

}
