package com.paypal.butterfly.utilities.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for {@link RemoveProperty}
 *
 * @author facarvalho
 */
public class RemovePropertyTest extends TransformationUtilityTestHelper {

    @Test
    public void successRemoveTest() throws IOException {
        RemoveProperty removeProperty = new RemoveProperty("bar").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = removeProperty.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("foo"), "foov");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void fileDoesNotExistTest() {
        RemoveProperty removeProperty = new RemoveProperty("foo").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = removeProperty.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertEquals(executionResult.getException(), null);
    }

}
