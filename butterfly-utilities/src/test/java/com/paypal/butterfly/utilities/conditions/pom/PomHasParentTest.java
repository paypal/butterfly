package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit Test(s) for {@link PomHasParent}
 *
 * @author apandilwar
 */
public class PomHasParentTest extends TransformationUtilityTestHelper {

    @Test
    public void testPomHasParentTrue() {
        PomHasParent pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomHasParent().relative("pom.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a parent POM exists in a Maven POM file");
    }

    @Test
    public void testPomHasParentFalse() {
        PomHasParent pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomHasParent().relative("pom_without_parent.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
    }

    @Test
    public void testPomHasParentInvalidXmlFile() {
        PomHasParent pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomHasParent().relative("/src/main/resources/dogs.yaml");
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a parent POM exists in a Maven POM file");

        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Exception occurred while checking if parent POM exists in /src/main/resources/dogs.yaml");
        Assert.assertNotNull(executionResult.getException().getCause());
        Assert.assertEquals(executionResult.getException().getCause().getClass(), XmlPullParserException.class);
        Assert.assertEquals(executionResult.getException().getCause().getMessage(), "only whitespace content allowed before start tag and not T (position: START_DOCUMENT seen T... @1:2) ");
    }

    @Test
    public void testPomHasParentIoException() {
        PomHasParent pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomHasParent().relative("/src/main/resources/dogs.yaml");
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a parent POM exists in a Maven POM file");

        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Exception occurred while checking if parent POM exists in /src/main/resources/dogs.yaml");
        Assert.assertNotNull(executionResult.getException().getCause());
        Assert.assertEquals(executionResult.getException().getCause().getClass(), XmlPullParserException.class);
        Assert.assertEquals(executionResult.getException().getCause().getMessage(), "only whitespace content allowed before start tag and not T (position: START_DOCUMENT seen T... @1:2) ");
    }

}
