package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit Test(s) for {@link PomParentMatch}
 *
 * @author apandilwar
 */
public class PomParentMatchTest extends TransformationUtilityTestHelper {

    @Test
    public void testPomParentMatchTrue() {
        PomParentMatch pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomParentMatch("com.test", "foo-parent").relative("pom.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a Maven POM file has a parent artifact 'com.test:foo-parent'");

        pomHasParent = new PomParentMatch("com.test", "foo-parent", "1.0").relative("pom.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());

        Assert.assertEquals(pomHasParent.getGroupId(), "com.test");
        Assert.assertEquals(pomHasParent.getArtifactId(), "foo-parent");
        Assert.assertEquals(pomHasParent.getVersion(), "1.0");
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a Maven POM file has a parent artifact 'com.test:foo-parent:1.0'");
    }

    @Test
    public void testPomParentMatchFalse() {
        PomParentMatch pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomParentMatch()
                .setGroupId("com.test")
                .setArtifactId("foo-parent")
                .setVersion("1.0")
                .relative("pom_without_parent.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());

        pomHasParent = new PomParentMatch("com.test", "foo-parent").relative("pom.xml");
        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
    }

    @Test
    public void testPomParentMatchInvalidXmlFile() {
        PomParentMatch pomHasParent;
        TUExecutionResult executionResult;

        pomHasParent = new PomParentMatch("com.test", "foo-parent").relative("/src/main/resources/dogs.yaml");
        Assert.assertEquals(pomHasParent.getDescription(), "Check if a Maven POM file has a parent artifact 'com.test:foo-parent'");
        Assert.assertEquals(pomHasParent.getGroupId(), "com.test");
        Assert.assertEquals(pomHasParent.getArtifactId(), "foo-parent");
        Assert.assertNull(pomHasParent.getVersion());

        executionResult = pomHasParent.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Exception occurred while checking if Maven POM file /src/main/resources/dogs.yaml has a parent artifact 'com.test:foo-parent'");
        Assert.assertNotNull(executionResult.getException().getCause());
        Assert.assertEquals(executionResult.getException().getCause().getClass(), XmlPullParserException.class);
        Assert.assertEquals(executionResult.getException().getCause().getMessage(), "only whitespace content allowed before start tag and not T (position: START_DOCUMENT seen T... @1:2) ");
    }

}
