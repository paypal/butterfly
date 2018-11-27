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
 * Unit test for {@link PomAddPropertyTest}
 *
 * @author facarvalho
 */
public class PomAddPropertyTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() {
        PomAddProperty pomAddProperty = new PomAddProperty().setPropertyName("foo").setPropertyValue("bar").relative("pom.xml");

        assertEquals(pomAddProperty.getPropertyName(), "foo");
        assertEquals(pomAddProperty.getPropertyValue(), "bar");
        assertEquals(pomAddProperty.getDescription(), "Add property foo=bar to POM file pom.xml");
        assertEquals(pomAddProperty.clone(), pomAddProperty);
    }

    @Test
    public void propertyAddedTest() throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        assertEquals(pomModelBeforeChange.getProperties().size(), 1);
        assertEquals(pomModelBeforeChange.getProperties().getProperty("encoding"), "UTF-8");

        PomAddProperty pomAddProperty = new PomAddProperty("foo", "bar").relative("pom.xml");
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getProperties().size(), 2);
        assertEquals(pomModelAfterChange.getProperties().getProperty("encoding"), "UTF-8");
        assertEquals(pomModelAfterChange.getProperties().getProperty("foo"), "bar");
    }

    @Test
    public void propertyAlreadyPresentErrorTest() throws IOException {
        PomAddProperty pomAddProperty = new PomAddProperty("encoding", "bar").relative("pom.xml").failIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Property encoding was not added to POM file pom.xml because it is already present");
        assertNull(executionResult.getException().getCause());
        assertNull(executionResult.getDetails());

        assertNotChangedFile("pom.xml");

        // Asserting that the default result type is ERROR, in case a property is not present
        assertEquals(new PomAddProperty("encoding", "bar").relative("pom.xml").execution(transformedAppFolder, transformationContext).getType(), TOExecutionResult.Type.ERROR);
    }

    @Test
    public void propertyAlreadyPresentNoOpTest() throws IOException {
        PomAddProperty pomAddProperty = new PomAddProperty("encoding", "bar").relative("pom.xml").noOpIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Property encoding was not added to POM file pom.xml because it is already present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void propertyAlreadyPresentNotAddWarnTest() throws IOException, SAXException, ParserConfigurationException {
        PomAddProperty pomAddProperty = new PomAddProperty("encoding", "bar").relative("pom.xml").warnNotAddIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);
        assertEqualsXml("pom.xml");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Property encoding was not added to POM file pom.xml because it is already present");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void propertyAlreadyPresentOverwriteWarnTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddProperty pomAddProperty = new PomAddProperty("encoding", "bar").relative("pom.xml").warnButAddIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Property encoding was already present in POM file pom.xml, but it was overwritten to bar");

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getProperties().size(), 1);
        assertEquals(pomModelAfterChange.getProperties().getProperty("encoding"), "bar");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void propertyAlreadyPresentOverwriteTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddProperty pomAddProperty = new PomAddProperty("encoding", "bar").relative("pom.xml").overwriteIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getProperties().size(), 1);
        assertEquals(pomModelAfterChange.getProperties().getProperty("encoding"), "bar");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void propertyNotPresentOverwriteTest() throws IOException, SAXException, ParserConfigurationException, XmlPullParserException {
        PomAddProperty pomAddProperty = new PomAddProperty("foo", "bar").relative("pom.xml").overwriteIfPresent();
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().size(), 0);

        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getProperties().size(), 2);
        assertEquals(pomModelAfterChange.getProperties().getProperty("encoding"), "UTF-8");
        assertEquals(pomModelAfterChange.getProperties().getProperty("foo"), "bar");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomAddProperty pomAddProperty = new PomAddProperty("zoo", "zee").relative("non_existent_file.xml");
        TOExecutionResult executionResult = pomAddProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

}