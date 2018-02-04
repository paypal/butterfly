package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class PomAddPluginTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() throws CloneNotSupportedException {
        PomAddPlugin pomAddPlugin = new PomAddPlugin("org.apache.maven.plugins", "maven-javadoc-plugin", "2.10.4").relative("pom.xml");

        assertEquals(pomAddPlugin.getDescription(), "Add plugin org.apache.maven.plugins:maven-javadoc-plugin to POM file pom.xml");
        assertEquals(pomAddPlugin.getVersion(), "2.10.4");
        assertEquals(pomAddPlugin.clone(), pomAddPlugin);
    }

	@Test
	public void addPluginWithVersionTest() throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
		assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
		assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        PomAddPlugin pomAddPlugin = new PomAddPlugin("org.apache.maven.plugins", "maven-javadoc-plugin", "2.10.4").relative("pom.xml");
		TOExecutionResult executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 2);
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        assertTrue(pomModelAfterChange.getBuild().getPlugins().contains(plugin));
        assertEquals(pomModelAfterChange.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-javadoc-plugin").getVersion(), "2.10.4");
	}

    @Test
    public void addPluginWithoutVersionTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        PomAddPlugin pomAddPlugin = new PomAddPlugin("org.apache.maven.plugins", "maven-javadoc-plugin").relative("pom.xml");
        TOExecutionResult executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 2);
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        assertTrue(pomModelAfterChange.getBuild().getPlugins().contains(plugin));
        assertNull(pomModelAfterChange.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-javadoc-plugin").getVersion());
    }

    @Test
    public void defaultIfPresentTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        // Trying to add the same plugin
        PomAddPlugin pomAddPlugin = new PomAddPlugin().setArtifact("org.codehaus.mojo:cobertura-maven-plugin").relative("pom.xml");
        TOExecutionResult executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertNull(executionResult.getDetails());
        assertEquals(executionResult.getException().getMessage(), "Plugin org.codehaus.mojo:cobertura-maven-plugin is already present in pom.xml");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelAfterChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        assertEquals(pomModelAfterChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void failureHandlingTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        TOExecutionResult executionResult;

        // Trying to add the same plugin
        PomAddPlugin pomAddPlugin = new PomAddPlugin().setArtifact("org.codehaus.mojo:cobertura-maven-plugin").relative("pom.xml");

        pomAddPlugin.failIfPresent();
        executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertNull(executionResult.getDetails());
        assertEquals(executionResult.getException().getMessage(), "Plugin org.codehaus.mojo:cobertura-maven-plugin is already present in pom.xml");
        assertNotChangedFile("pom.xml");

        pomAddPlugin.noOpIfPresent();
        executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertNotChangedFile("pom.xml");

        pomAddPlugin.overwriteIfPresent();
        executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());

        pomAddPlugin.warnNotAddIfPresent();
        executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getDetails());
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Plugin org.codehaus.mojo:cobertura-maven-plugin is already present in pom.xml");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");

        pomAddPlugin.warnButAddIfPresent();
        executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertEquals(executionResult.getDetails(), "Plugin org.codehaus.mojo:cobertura-maven-plugin has been added to POM file /pom.xml");
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Plugin org.codehaus.mojo:cobertura-maven-plugin is already present in pom.xml");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

}