package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Unit tests for {@link CompareFiles}
 *
 * @author facarvalho
 */
public class CompareFilesTest extends TransformationUtilityTestHelper {

    @Test
    public void compareFilesEqualSameFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/application.properties"));
        CompareFiles compareFiles = new CompareFiles("ATR").relative("/src/main/resources/application.properties");
        TUExecutionResult executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(compareFiles.getDescription(), "Compare file /src/main/resources/application.properties to another one, return true only if their contents are equal");
    }

    @Test
    public void compareFilesEqualDifferentFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/more_yaml/dogs.yaml"));
        CompareFiles compareFiles = new CompareFiles().setAttribute("ATR").relative("/src/main/resources/dogs.yaml");
        TUExecutionResult executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(compareFiles.getDescription(), "Compare file /src/main/resources/dogs.yaml to another one, return true only if their contents are equal");
    }

    @Test
    public void compareFilesDifferentFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/application.properties"));
        CompareFiles compareFiles = new CompareFiles().setAttribute("ATR").relative("/src/main/resources/dogs.yaml");
        TUExecutionResult executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(compareFiles.getDescription(), "Compare file /src/main/resources/dogs.yaml to another one, return true only if their contents are equal");
    }

    @Test
    public void compareFilesInexistentFilesTest() {
        CompareFiles compareFiles;
        TUExecutionResult executionResult;

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/inexistent_file"));
        compareFiles = new CompareFiles().setAttribute("ATR").relative("/src/main/resources/dogs.yaml");
        executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/inexistent_file"));
        compareFiles = new CompareFiles().setAttribute("ATR").relative("/src/main/resources/another_inexistent_file.yaml");
        executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/dogs.yaml"));
        compareFiles = new CompareFiles().setAttribute("ATR").relative("/src/main/resources/another_inexistent_file.yaml");
        executionResult = compareFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
    }

}
