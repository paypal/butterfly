package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Unit tests for {@link FindFile}
 *
 * @author facarvalho
 */
public class FindFileTest extends TransformationUtilityTestHelper {

    @Test
    public void fileFoundTest() throws URISyntaxException {
        FindFile findFile =  new FindFile("dogs.yaml");
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        File file = (File) executionResult.getValue();
        Assert.assertEquals(file, new File(transformedAppFolder, "/src/main/resources/dogs.yaml"));
        Assert.assertEquals(findFile.getDescription(), "Find file named dogs.yaml under root of application");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNotFoundNullTest() throws URISyntaxException {
        FindFile findFile =  new FindFile().setFileName("cats.yaml");
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(findFile.getDescription(), "Find file named cats.yaml under root of application");
        Assert.assertFalse(findFile.isFailIfNotFound());
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNotFoundErrorTest() throws URISyntaxException {
        FindFile findFile =  new FindFile("cats.yaml").failIfNotFound(true);
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(findFile.getDescription(), "Find file named cats.yaml under root of application");
        Assert.assertTrue(findFile.isFailIfNotFound());
        Assert.assertEquals(findFile.getFileName(), "cats.yaml");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "No file named 'cats.yaml' has been found");
    }

    @Test
    public void errorTest() throws URISyntaxException {
        FindFile findFile =  new FindFile();
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(findFile.getDescription(), "Find file named null under root of application");
        Assert.assertFalse(findFile.isFailIfNotFound());
        Assert.assertNull(findFile.getFileName());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File name has not been set");
    }

}
