package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class PomAddPluginTest extends TransformationUtilityTestHelper {

	@Test
	public void addPluginWithVersionTest() throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
		Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
		Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

		PomAddPlugin pomAddPlugin = new PomAddPlugin("org.apache.maven.plugins", "maven-javadoc-plugin", "2.10.4").relative("pom.xml");
		TOExecutionResult executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
		Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
		Assert.assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 2);
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        Assert.assertTrue(pomModelAfterChange.getBuild().getPlugins().contains(plugin));
        Assert.assertEquals(pomModelAfterChange.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-javadoc-plugin").getVersion(), "2.10.4");
	}

    @Test
    public void addPluginWithoutVersionTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
        Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        Assert.assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        PomAddPlugin pomAddPlugin = new PomAddPlugin("org.apache.maven.plugins", "maven-javadoc-plugin").relative("pom.xml");
        TOExecutionResult executionResult = pomAddPlugin.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        Assert.assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 2);
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        Assert.assertTrue(pomModelAfterChange.getBuild().getPlugins().contains(plugin));
        Assert.assertNull(pomModelAfterChange.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-javadoc-plugin").getVersion());
    }

}