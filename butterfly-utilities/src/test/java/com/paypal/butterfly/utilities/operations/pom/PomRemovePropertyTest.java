package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test for {@link PomRemovePropertyTest}
 *
 * @author facarvalho
 */
public class PomRemovePropertyTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() {
        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty().setPropertyName("encoding").relative("pom.xml");

        assertEquals(pomRemoveProperty.getPropertyName(), "encoding");
        assertEquals(pomRemoveProperty.getDescription(), "Remove property encoding from POM file pom.xml");
        assertEquals(pomRemoveProperty.clone(), pomRemoveProperty);
    }

    @Test
    public void propertyRemovedTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getProperties().size(), 1);
        assertEquals(pomModelBeforeChange.getProperties().getProperty("encoding"), "UTF-8");

        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty("encoding").relative("pom.xml");
        assertEquals(pomRemoveProperty.getPropertyName(), "encoding");
        TOExecutionResult executionResult = pomRemoveProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getProperties().size(), 0);
    }

    @Test
    public void propertyNotPresentErrorTest() throws IOException {
        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty("zoo").relative("pom.xml").failIfNotPresent();
        TOExecutionResult executionResult = pomRemoveProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Property zoo has not been removed from POM file /pom.xml because it is not present");
        assertNull(executionResult.getException().getCause());
        assertNull(executionResult.getDetails());

        assertNotChangedFile("pom.xml");

        // Asserting that the default resut type is ERROR, in case a property is not present
        assertEquals(new PomRemoveProperty("zoo").relative("pom.xml").execution(transformedAppFolder, transformationContext).getType(), TOExecutionResult.Type.ERROR);
    }

    @Test
    public void propertyNotPresentNoOpTest() throws IOException {
        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty("zoo").relative("pom.xml").noOpIfNotPresent();
        TOExecutionResult executionResult = pomRemoveProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Property zoo has not been removed from POM file /pom.xml because it is not present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void propertyNotPresentWarnTest() throws IOException, SAXException, ParserConfigurationException {
        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty("zoo").relative("pom.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomRemoveProperty.execution(transformedAppFolder, transformationContext);
        assertEqualsXml("pom.xml");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Property zoo has not been removed from POM file /pom.xml because it is not present");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomRemoveProperty pomRemoveProperty = new PomRemoveProperty("zoo").relative("non_existent_file.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomRemoveProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

}