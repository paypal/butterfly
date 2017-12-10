package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for PomRemovePlugin TO
 *
 * @author facarvalho
 */
public class PomRemovePluginTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() throws CloneNotSupportedException {
        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("org.apache.maven.plugins", "maven-javadoc-plugin").relative("pom.xml");

        assertEquals(pomRemovePlugin.getDescription(), "Remove plugin org.apache.maven.plugins:maven-javadoc-plugin from POM file pom.xml");
        assertEquals(pomRemovePlugin.clone(), pomRemovePlugin);
    }

    @Test
    public void pluginRemovedTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().size(), 1);
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getGroupId(), "org.codehaus.mojo");
        assertEquals(pomModelBeforeChange.getBuild().getPlugins().get(0).getArtifactId(), "cobertura-maven-plugin");

        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml");
        TOExecutionResult executionResult = pomRemovePlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getBuild().getPlugins().size(), 0);
    }

    @Test
    public void pluginNotPresentErrorTest() throws IOException {
        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("com.zoo", "zoo").relative("pom.xml");
        TOExecutionResult executionResult = pomRemovePlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void pluginNotPresentNoOpTest() throws IOException {
        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("com.zoo", "zoo").relative("pom.xml").noOpIfNotPresent();
        TOExecutionResult executionResult = pomRemovePlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void pluginNotPresentWarnTest() throws IOException, XmlPullParserException, ParserConfigurationException, SAXException {
        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("com.zoo", "zoo").relative("pom.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomRemovePlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);

        assertEqualsXml("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomRemovePlugin pomRemovePlugin = new PomRemovePlugin("com.foo", "boo").relative("application_zeta.properties");
        TOExecutionResult executionResult = pomRemovePlugin.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
    }

}