package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import com.paypal.butterfly.utilities.operations.pom.PomChangeParentVersion;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Unit test class for {@link PomChangeParentVersion}
 *
 * @author facarvalho
 */
public class PomChangeParentVersionTest extends TransformationUtilityTestHelper {

    @Test
    public void changeVersionTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getParent().getVersion(), "1.0");

        PomChangeParentVersion pomChangeParentVersion = new PomChangeParentVersion("2.0").relative("pom.xml");

        assertEquals(pomChangeParentVersion.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParentVersion.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        Assert.assertEquals(pomChangeParentVersion.getDescription(), "Change artifact's parent version in POM file pom.xml");
        Assert.assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getParent().getVersion(), "2.0");
    }

    @Test
    public void noOpTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParentVersion pomChangeParentVersion = new PomChangeParentVersion("2.0").relative("/src/main/resources/no_parent_pom.xml").noOpIfNotPresent();

        assertEquals(pomChangeParentVersion.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParentVersion.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertEquals(pomChangeParentVersion.getDescription(), "Change artifact's parent version in POM file /src/main/resources/no_parent_pom.xml");
        Assert.assertNull(executionResult.getException());

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());

        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void warnTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParentVersion pomChangeParentVersion = new PomChangeParentVersion().relative("/src/main/resources/no_parent_pom.xml").warnIfNotPresent();
        pomChangeParentVersion.setVersion("2.0");

        assertEquals(pomChangeParentVersion.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParentVersion.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        Assert.assertEquals(pomChangeParentVersion.getDescription(), "Change artifact's parent version in POM file /src/main/resources/no_parent_pom.xml");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getWarnings().size(), 1);
        Assert.assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        Assert.assertEquals(executionResult.getWarnings().get(0).getMessage(), "Pom file /src/main/resources/no_parent_pom.xml does not have a parent");

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());

        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void noParentFailTest() throws IOException, XmlPullParserException, CloneNotSupportedException {
        Model pomModelBeforeChange = getOriginalPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelBeforeChange.getParent());

        PomChangeParentVersion pomChangeParentVersion = new PomChangeParentVersion("2.0").relative("/src/main/resources/no_parent_pom.xml");

        assertEquals(pomChangeParentVersion.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParentVersion.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(pomChangeParentVersion.getDescription(), "Change artifact's parent version in POM file /src/main/resources/no_parent_pom.xml");
        Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Pom file /src/main/resources/no_parent_pom.xml does not have a parent");

        Model pomModelAfterChange = getTransformedPomModel("/src/main/resources/no_parent_pom.xml");
        assertNull(pomModelAfterChange.getParent());

        executionResult = pomChangeParentVersion.clone().failIfNotPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);

        assertNotChangedFile("/src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void readErrorFailTest() throws IOException, XmlPullParserException, CloneNotSupportedException {
        PomChangeParentVersion pomChangeParentVersion = new PomChangeParentVersion("2.0").relative("/src/main/resources/dogs.yaml");

        assertEquals(pomChangeParentVersion.getVersion(), "2.0");

        TOExecutionResult executionResult = pomChangeParentVersion.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(pomChangeParentVersion.getDescription(), "Change artifact's parent version in POM file /src/main/resources/dogs.yaml");
        Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "An error happened when reading XML file /src/main/resources/dogs.yaml");
        assertNotChangedFile("/src/main/resources/dogs.yaml");

        executionResult = pomChangeParentVersion.clone().failIfNotPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertNotChangedFile("/src/main/resources/dogs.yaml");
    }

}
