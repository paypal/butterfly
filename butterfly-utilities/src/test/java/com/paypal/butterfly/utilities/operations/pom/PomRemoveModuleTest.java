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
 * Unit test for {@link PomRemoveModuleTest}
 *
 * @author facarvalho
 */
public class PomRemoveModuleTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() {
        PomRemoveModule pomRemoveModule = new PomRemoveModule().setModuleName("module1").relative("pom.xml");

        assertEquals(pomRemoveModule.getModuleName(), "module1");
        assertEquals(pomRemoveModule.getDescription(), "Remove module module1 from POM file pom.xml");
        assertEquals(pomRemoveModule.clone(), pomRemoveModule);
    }

    @Test
    public void moduleRemovedTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getModules().size(), 1);
        assertTrue(pomModelBeforeChange.getModules().contains("module1"));

        PomRemoveModule pomRemoveModule = new PomRemoveModule("module1").relative("pom.xml");
        assertEquals(pomRemoveModule.getModuleName(), "module1");
        TOExecutionResult executionResult = pomRemoveModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getModules().size(), 0);
    }

    @Test
    public void moduleNotPresentErrorTest() throws IOException {
        PomRemoveModule pomRemoveModule = new PomRemoveModule("zoo").relative("pom.xml").failIfNotPresent();
        TOExecutionResult executionResult = pomRemoveModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Module zoo has not been removed from POM file /pom.xml because it is not present");
        assertNull(executionResult.getException().getCause());
        assertNull(executionResult.getDetails());

        assertNotChangedFile("pom.xml");

        // Asserting that the default result type is ERROR, in case a module is not present
        assertEquals(new PomRemoveModule("zoo").relative("pom.xml").execution(transformedAppFolder, transformationContext).getType(), TOExecutionResult.Type.ERROR);
    }

    @Test
    public void moduleNotPresentNoOpTest() throws IOException {
        PomRemoveModule pomRemoveModule = new PomRemoveModule("zoo").relative("pom.xml").noOpIfNotPresent();
        TOExecutionResult executionResult = pomRemoveModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Module zoo has not been removed from POM file /pom.xml because it is not present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void moduleNotPresentWarnTest() throws IOException, SAXException, ParserConfigurationException {
        PomRemoveModule pomRemoveModule = new PomRemoveModule("zoo").relative("pom.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomRemoveModule.execution(transformedAppFolder, transformationContext);
        assertEqualsXml("pom.xml");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Module zoo has not been removed from POM file /pom.xml because it is not present");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomRemoveModule pomRemoveModule = new PomRemoveModule("zoo").relative("non_existent_file.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomRemoveModule.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

}