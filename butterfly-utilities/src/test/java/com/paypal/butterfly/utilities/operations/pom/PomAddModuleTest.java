package com.paypal.butterfly.utilities.operations.pom;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit test for {@link PomAddModuleTest}
 *
 * @author facarvalho
 */
public class PomAddModuleTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() {
        PomAddModule pomAddModule = new PomAddModule().setModuleName("foo").relative("pom.xml");

        assertEquals(pomAddModule.getModuleName(), "foo");
        assertEquals(pomAddModule.getDescription(), "Add module foo to POM file pom.xml");
        assertEquals(pomAddModule.clone(), pomAddModule);
    }

    @Test
    public void moduleAddedTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getModules().size(), 1);
        assertTrue(pomModelBeforeChange.getModules().contains("module1"));

        PomAddModule pomAddModule = new PomAddModule("foo").relative("pom.xml");
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getModules().size(), 2);
        assertTrue(pomModelAfterChange.getModules().contains("module1"));
        assertTrue(pomModelAfterChange.getModules().contains("foo"));
    }

    @Test
    public void moduleAlreadyPresentErrorTest() throws IOException {
        PomAddModule pomAddModule = new PomAddModule("module1").relative("pom.xml").failIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Module module1 was not added to POM file pom.xml because it is already present");
        assertNull(executionResult.getException().getCause());
        assertNull(executionResult.getDetails());

        assertNotChangedFile("pom.xml");

        // Asserting that the default result type is ERROR, in case a module is not present
        assertEquals(new PomAddModule("module1").relative("pom.xml").execution(transformedAppFolder, transformationContext).getType(), TOExecutionResult.Type.ERROR);
    }

    @Test
    public void moduleAlreadyPresentNoOpTest() throws IOException {
        PomAddModule pomAddModule = new PomAddModule("module1").relative("pom.xml").noOpIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Module module1 was not added to POM file pom.xml because it is already present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void moduleAlreadyPresentNotAddWarnTest() throws IOException, SAXException, ParserConfigurationException {
        PomAddModule pomAddModule = new PomAddModule("module1").relative("pom.xml").warnNotAddIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);
        assertEqualsXml("pom.xml");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Module module1 was not added to POM file pom.xml because it is already present");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void moduleAlreadyPresentOverwriteWarnTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddModule pomAddModule = new PomAddModule("module1").relative("pom.xml").warnButAddIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Module module1 was already present in POM file pom.xml");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getModules().size(), 1);
        assertTrue(pomModelAfterChange.getModules().contains("module1"));

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void moduleAlreadyPresentOverwriteTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddModule pomAddModule = new PomAddModule("module1").relative("pom.xml").overwriteIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getModules().size(), 1);
        assertTrue(pomModelAfterChange.getModules().contains("module1"));

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void moduleNotPresentOverwriteTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddModule pomAddModule = new PomAddModule("foo").relative("pom.xml").overwriteIfPresent();
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getModules().size(), 2);
        assertTrue(pomModelAfterChange.getModules().contains("module1"));
        assertTrue(pomModelAfterChange.getModules().contains("foo"));

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomAddModule pomAddModule = new PomAddModule("foo").relative("non_existent_file.xml");
        TOExecutionResult executionResult = pomAddModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

}