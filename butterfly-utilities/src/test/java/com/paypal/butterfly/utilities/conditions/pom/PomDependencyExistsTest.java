package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
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

}
