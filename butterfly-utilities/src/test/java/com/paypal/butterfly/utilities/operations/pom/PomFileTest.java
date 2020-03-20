package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import com.paypal.butterfly.utilities.pom.PomModel;
import org.apache.maven.model.Model;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link PomFile}
 *
 * @author facarvalho
 */
public class PomFileTest extends TransformationUtilityTestHelper {

    private static Model model;

    @BeforeClass
    private static void setUpModel() {
        model = new Model();
        model.setGroupId("com.test");
        model.setArtifactId("foo");
        model.setVersion("1.0");
    }

    @Test
    public void objectTest() {
        assertFalse(new File(transformedAppFolder, "blah/pom.xml").exists());

        PomFile pomFile = new PomFile(model).relative("blah");
        assertNull(pomFile.getAttribute());
        assertEquals(pomFile.getModel(), model);
        assertEquals(pomFile.getDescription(), "Writes a pom.xml file at blah given a Model object");

        TOExecutionResult executionResult = pomFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        checkCreatedPomFile("blah/pom.xml");
    }

    @Test
    public void attributeTest() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(model);

        assertFalse(new File(transformedAppFolder, "blah/pom.xml").exists());

        PomFile pomFile = new PomFile().setAttribute("ATT").relative("blah");
        assertEquals(pomFile.getAttribute(), "ATT");
        assertNull(pomFile.getModel());
        assertEquals(pomFile.getDescription(), "Writes a pom.xml file at blah given a Model object kept at transformation context attribute ATT");

        TOExecutionResult executionResult = pomFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        checkCreatedPomFile("blah/pom.xml");
    }

    @Test
    public void noModelNoAttributeFailTest() {
        TOExecutionResult executionResult = new PomFile().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Neither transformation context attribute nor Model object were specified");
    }

    @Test
    public void nonExistentAttributeFailTest() {
        TOExecutionResult executionResult = new PomFile("ATT").execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute ATT does not exist");
    }

    @Test
    public void nullAttributeFailTest() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn(null);

        TOExecutionResult executionResult = new PomFile("ATT").execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute ATT is null");
    }

    @Test
    public void invalidAttributeFailTest() {
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.get("ATT")).thenReturn("");

        TOExecutionResult executionResult = new PomFile("ATT").execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute ATT is not a Model object, but java.lang.String");
    }

    @Test
    public void invalidFolderFailTest() {
        TOExecutionResult executionResult = new PomFile(model).relative("gigs").execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "There was an error when creating pom.xml file at gigs, double check if that directory exists");
    }

    @Test
    public void existentErrorFailTest() {
        TOExecutionResult executionResult = new PomFile(model).relative("/").execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "There is already a pom file at /");
    }

    @Test
    public void existentNoOpTest() {
        TOExecutionResult executionResult = new PomFile(model).relative("/").noOpIfPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "There is already a pom file at /");
    }

    @Test
    public void existentWarnNotAddTest() {
        TOExecutionResult executionResult = new PomFile(model).relative("/").warnNotAddIfPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "There is already a pom file at /");
    }

    @Test
    public void existentWarnButAddTest() {
        assertTrue(new File(transformedAppFolder, "pom.xml").exists());

        TOExecutionResult executionResult = new PomFile(model).relative("/").warnButAddIfPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "There is already a pom file at /");
        assertEquals(executionResult.getDetails(), "Pom file overwritten at /");

        checkCreatedPomFile("pom.xml");
    }

    @Test
    public void existentOverwriteTest() {
        assertTrue(new File(transformedAppFolder, "pom.xml").exists());

        TOExecutionResult executionResult = new PomFile(model).relative("/").overwriteIfPresent().execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);
        assertEquals(executionResult.getDetails(), "Pom file overwritten at /");

        checkCreatedPomFile("pom.xml");
    }

    private void checkCreatedPomFile(String fileRelativePath) {
        assertTrue(new File(transformedAppFolder, fileRelativePath).exists());

        TUExecutionResult er = (TUExecutionResult) new PomModel().relative(fileRelativePath).perform(transformedAppFolder, transformationContext).getExecutionResult();
        Model createdPomModel = (Model) er.getValue();

        assertEquals(model.getGroupId(), createdPomModel.getGroupId());
        assertEquals(model.getArtifactId(), createdPomModel.getArtifactId());
        assertEquals(model.getVersion(), createdPomModel.getVersion());
    }

}
