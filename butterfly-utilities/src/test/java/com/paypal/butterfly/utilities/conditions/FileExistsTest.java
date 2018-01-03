package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link FileExists}
 *
 * @author facarvalho
 */
public class FileExistsTest extends TransformationUtilityTestHelper {

    @Test
    public void fileExistsTest() {
        FileExists fileExists = new FileExists().relative("/src/main/resources/application.properties");
        TUExecutionResult executionResult = fileExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(fileExists.getDescription(), "Check if file or folder '/src/main/resources/application.properties' exists");
    }

    @Test
    public void fileDoesntExistTest() {
        FileExists fileExists = new FileExists().relative("/src/main/coco/notes.txt");
        TUExecutionResult executionResult = fileExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(fileExists.getDescription(), "Check if file or folder '/src/main/coco/notes.txt' exists");
    }

}
