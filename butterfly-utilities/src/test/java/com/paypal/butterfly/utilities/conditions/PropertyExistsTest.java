package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit tests for {@link PropertyExists}
 *
 * @author facarvalho
 */
public class PropertyExistsTest extends TransformationUtilityTestHelper {

    @BeforeMethod
    public void setUp() throws IOException {
        Properties properties = getProperties("/src/main/resources/application.properties");
        Assert.assertNotNull(properties.getProperty("foo"));
        Assert.assertNull(properties.getProperty("blah"));
    }

    @Test
    public void propertyExistsTest() {
        PropertyExists propertyExists;
        TUExecutionResult executionResult;

        propertyExists = new PropertyExists("foo").relative("/src/main/resources/application.properties");
        executionResult = propertyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(propertyExists.getPropertyName(), "foo");
        Assert.assertNull(propertyExists.getPropertyNameRegex());
        Assert.assertEquals(propertyExists.getDescription(), "Check if property 'foo' exists in a property file");

        propertyExists = new PropertyExists().setPropertyNameRegex(".oo").relative("/src/main/resources/application.properties");
        executionResult = propertyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertNull(propertyExists.getPropertyName());
        Assert.assertEquals(propertyExists.getPropertyNameRegex(), ".oo");
        Assert.assertEquals(propertyExists.getDescription(), "Check if property '.oo' exists in a property file");
    }

    @Test
    public void propertyDoesntExistsTest() {
        PropertyExists propertyExists;
        TUExecutionResult executionResult;

        propertyExists = new PropertyExists("blah").relative("/src/main/resources/application.properties");
        executionResult = propertyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(propertyExists.getPropertyName(), "blah");
        Assert.assertNull(propertyExists.getPropertyNameRegex());

        propertyExists = new PropertyExists().setPropertyNameRegex(".lah").relative("/src/main/resources/application.properties");
        executionResult = propertyExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertNull(propertyExists.getPropertyName());
        Assert.assertEquals(propertyExists.getPropertyNameRegex(), ".lah");
    }

}
