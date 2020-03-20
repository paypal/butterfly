package com.paypal.butterfly.utilities.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link AddProperty}
 *
 * @author facarvalho
 */
public class AddPropertyTest extends TransformationUtilityTestHelper {

    @Test
    public void successAddToEmptyFile() throws IOException {
        final String relativeFilePath = "/src/main/resources/application-empty.properties";
        final String testKey = "testkey";
        final String testValue = "testvalue";

        AddProperty addProperty = new AddProperty().setPropertyName(testKey).setPropertyValue(testValue).relative(relativeFilePath);

        TOExecutionResult executionResult = addProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile(relativeFilePath);
        assertLineCount(relativeFilePath, 1);

        Properties properties = getProperties(relativeFilePath);

        assertEquals(properties.size(), 1);
        assertEquals(properties.getProperty(testKey), testValue);

    }

    @Test
    public void successAddTest() throws IOException {
        AddProperty addProperty = new AddProperty().setPropertyName("zoo").setPropertyValue("zoov").relative("/src/main/resources/application.properties");
        assertEquals(addProperty.getPropertyName(), "zoo");
        assertEquals(addProperty.getPropertyValue(), "zoov");
        assertEquals(addProperty.getDescription(), "Add new property (zoo = zoov) to file /src/main/resources/application.properties");

        TOExecutionResult executionResult = addProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        assertEquals(properties.size(), 4);
        assertEquals(properties.getProperty("bar"), "barv");
        assertEquals(properties.getProperty("foo"), "foov");
        assertEquals(properties.getProperty("foofoo"), "foofoov");
        assertEquals(properties.getProperty("zoo"), "zoov");
    }

    @Test
    public void successSetTest() throws IOException {
        AddProperty addProperty = new AddProperty("foo", "boo").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = addProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertSameLineCount("/src/main/resources/application.properties");

        Properties properties = getProperties("/src/main/resources/application.properties");

        assertEquals(properties.size(), 3);
        assertEquals(properties.getProperty("bar"), "barv");
        assertEquals(properties.getProperty("foo"), "boo");
        assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void fileDoesNotExistTest() {
        AddProperty addProperty = new AddProperty("foo", "boo").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = addProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Property file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "/src/main/resources/application_zeta.properties").getAbsolutePath() + " (No such file or directory)");
    }

}
