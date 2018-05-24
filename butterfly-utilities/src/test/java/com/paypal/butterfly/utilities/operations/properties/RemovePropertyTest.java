package com.paypal.butterfly.utilities.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Unit test for {@link RemoveProperty}
 *
 * @author facarvalho
 */
public class RemovePropertyTest extends TransformationUtilityTestHelper {

    @Test
    public void successRemoveTest() throws IOException {
        RemoveProperty removeProperty = new RemoveProperty().setPropertyName("bar").relative("/src/main/resources/application.properties");
        assertEquals(removeProperty.getPropertyName(), "bar");
        assertEquals(removeProperty.getDescription(), "Remove property bar from file /src/main/resources/application.properties");

        TOExecutionResult executionResult = removeProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        assertEquals(properties.size(), 2);
        assertEquals(properties.getProperty("foo"), "foov");
        assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void propertyDoesntExistTest() throws IOException {
        RemoveProperty removeProperty = new RemoveProperty().setPropertyName("boo").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = removeProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertNotNull(executionResult.getWarnings());
        assertEquals(executionResult.getWarnings().size(), 1);
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Property 'boo' has not been removed from '/src/main/resources/application.properties' because it is not present");

        assertNotChangedFile("/src/main/resources/application.properties");
    }

    @Test
    public void fileDoesNotExistTest() {
        RemoveProperty removeProperty = new RemoveProperty("foo").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = removeProperty.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertEquals(executionResult.getException(), null);
    }

}
