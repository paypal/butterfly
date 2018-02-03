package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Unit test class for {@link PomChangeParent}
 *
 * @author facarvalho
 */
public class PomChangeParentTest extends TransformationUtilityTestHelper {

    @Test
    public void changeVersionTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomChangeParent pomChangeParent = new PomChangeParent("2.0").relative("pom.xml");

        assertNull(pomChangeParent.getGroupId());
        assertNull(pomChangeParent.getArtifactId());
        assertEquals(pomChangeParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file pom.xml");
        assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");
    }

    @Test
    public void changeParentTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");

        assertEquals(pomModelBeforeChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelBeforeChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomChangeParent pomChangeParent = new PomChangeParent().setGroupId("com.newgroupid").setArtifactId("newartifactid").setVersion("2.0").relative("pom.xml");

        assertEquals(pomChangeParent.getGroupId(), "com.newgroupid");
        assertEquals(pomChangeParent.getArtifactId(), "newartifactid");
        assertEquals(pomChangeParent.getVersion(), "2.0");


        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file pom.xml");
        assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.newgroupid");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "newartifactid");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");
    }

    @Test
    public void failTest() throws IOException, XmlPullParserException, CloneNotSupportedException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParent pomChangeParent = new PomChangeParent("2.0").relative("/src/main/resources/no_parent_pom.xml");

        assertNull(pomChangeParent.getGroupId());
        assertNull(pomChangeParent.getArtifactId());
        assertEquals(pomChangeParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file /src/main/resources/no_parent_pom.xml");
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Pom file /src/main/resources/no_parent_pom.xml does not have a parent");

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());

        executionResult = pomChangeParent.clone().failIfNotPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);

        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void noOpTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParent pomChangeParent = new PomChangeParent("2.0").relative("/src/main/resources/no_parent_pom.xml").noOpIfNotPresent();

        assertNull(pomChangeParent.getGroupId());
        assertNull(pomChangeParent.getArtifactId());
        assertEquals(pomChangeParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file /src/main/resources/no_parent_pom.xml");
        assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());

        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void warnTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParent pomChangeParent = new PomChangeParent("2.0").relative("/src/main/resources/no_parent_pom.xml").warnIfNotPresent();

        assertNull(pomChangeParent.getGroupId());
        assertNull(pomChangeParent.getArtifactId());
        assertEquals(pomChangeParent.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file /src/main/resources/no_parent_pom.xml");
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Pom file /src/main/resources/no_parent_pom.xml does not have a parent");

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());


// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void invalidOperationTest() throws IOException, XmlPullParserException {
        // You have to either set the version only, or the group id AND artifact id AND version.
        // Here only the group id is being set
        PomChangeParent pomChangeParent = new PomChangeParent().setGroupId("com.test").relative("pom.xml");

        assertEquals(pomChangeParent.getGroupId(), "com.test");
        assertNull(pomChangeParent.getArtifactId());
        assertNull(pomChangeParent.getVersion());

        TOExecutionResult executionResult = pomChangeParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(pomChangeParent.getDescription(), "Change parent artifact in POM file pom.xml");
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Invalid POM parent transformation operation");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getGroupId(), "com.test");
        assertEquals(pomModelAfterChange.getParent().getArtifactId(), "foo-parent");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "1.0");

        assertNotChangedFile("pom.xml");
    }

}
