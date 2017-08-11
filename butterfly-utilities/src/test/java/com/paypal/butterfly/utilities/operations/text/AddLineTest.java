package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for {@link AddLine}
 *
 * @author facarvalho
 */
public class AddLineTest extends TransformationUtilityTestHelper {

    @Test
    public void successAddTest() throws IOException {
        AddLine addLine = new AddLine("zoo=zoov").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = addLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 4);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
        Assert.assertEquals(properties.getProperty("foo"), "foov");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
        Assert.assertEquals(properties.getProperty("zoo"), "zoov");
    }

    @Test
    public void fileDoesNotExistTest() {
        AddLine addLine = new AddLine("zoo=zoov").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = addLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
    }

}
