package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link PomDependencyExists}
 *
 * @author facarvalho
 */
public class PomDependencyExistsTest extends TransformationUtilityTestHelper {

    @Test
    public void dependencyExistsTest() {
        PomDependencyExists pomDependencyExists;
        TUExecutionResult executionResult;

        pomDependencyExists = new PomDependencyExists("xmlunit", "xmlunit").relative("pom.xml");
        executionResult = pomDependencyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(pomDependencyExists.getDescription(), "Check if dependency 'xmlunit:xmlunit' exists in a POM file");

        pomDependencyExists = new PomDependencyExists("xmlunit", "xmlunit", "1.5").relative("pom.xml");
        executionResult = pomDependencyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());

        Assert.assertEquals(pomDependencyExists.getGroupId(), "xmlunit");
        Assert.assertEquals(pomDependencyExists.getArtifactId(), "xmlunit");
        Assert.assertEquals(pomDependencyExists.getVersion(), "1.5");
        Assert.assertEquals(pomDependencyExists.getDescription(), "Check if dependency 'xmlunit:xmlunit:1.5' exists in a POM file");
    }

    @Test
    public void dependencyDoesntExistsTest() {
        PomDependencyExists pomDependencyExists;
        TUExecutionResult executionResult;

        pomDependencyExists = new PomDependencyExists()
                .setGroupId("xmlunit")
                .setArtifactId("xmlunit")
                .setVersion("1.6")
                .relative("pom.xml");
        executionResult = pomDependencyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());

        pomDependencyExists = new PomDependencyExists("org.slf4j", "slf4j-api").relative("pom.xml");
        executionResult = pomDependencyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
    }

    @Test
    public void invalidXmlFileTest() {
        PomDependencyExists pomDependencyExists;
        TUExecutionResult executionResult;

        pomDependencyExists = new PomDependencyExists("xmlunit", "xmlunit").relative("/src/main/resources/dogs.yaml");
        Assert.assertEquals(pomDependencyExists.getDescription(), "Check if dependency 'xmlunit:xmlunit' exists in a POM file");
        Assert.assertEquals(pomDependencyExists.getGroupId(), "xmlunit");
        Assert.assertEquals(pomDependencyExists.getArtifactId(), "xmlunit");
        Assert.assertNull(pomDependencyExists.getVersion());

        executionResult = pomDependencyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Exception happened when checking if POM dependency xmlunit:xmlunit exists in /src/main/resources/dogs.yaml");
        Assert.assertNotNull(executionResult.getException().getCause());
        Assert.assertEquals(executionResult.getException().getCause().getClass(), XmlPullParserException.class);
        Assert.assertEquals(executionResult.getException().getCause().getMessage(), "only whitespace content allowed before start tag and not T (position: START_DOCUMENT seen T... @1:1) ");
    }

}
