package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Unit tests for {@link FindFile}
 *
 * @author facarvalho
 */
public class FindFileTest extends TransformationUtilityTestHelper {

    @Test
    public void fileFoundTest() {
        FindFile findFile =  new FindFile("dogs.yaml").relative("/src/main/resources/more_yaml");
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        File file = (File) executionResult.getValue();
        Assert.assertEquals(file, new File(transformedAppFolder, "/src/main/resources/more_yaml/dogs.yaml"));
        Assert.assertEquals(findFile.getDescription(), "Find file named dogs.yaml under /src/main/resources/more_yaml");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNotFoundNullTest() {
        FindFile findFile =  new FindFile().setFileName("cats.yaml");
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(findFile.getDescription(), "Find file named cats.yaml under root of application");
        Assert.assertFalse(findFile.isFailIfNotFound());
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNotFoundErrorTest() {
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
    public void multipleFilesFoundTest() {
        FindFile findFile =  new FindFile("dogs.yaml");
        TUExecutionResult executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(findFile.getDescription(), "Find file named dogs.yaml under root of application");
        Assert.assertFalse(findFile.isFailIfNotFound());
        Assert.assertEquals(findFile.getFileName(), "dogs.yaml");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "More than one file named dogs.yaml have been found");
    }

    @Test
    public void noFileNameErrorTest() {
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

    @Test
    public void noSearchFolderErrorTest() {
        FindFile findFile;
        TUExecutionResult executionResult;

        findFile =  new FindFile("myfile.txt").relative("no_existent_folder").failIfNotFound(true);
        executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());

        Assert.assertEquals(findFile.getDescription(), "Find file named myfile.txt under no_existent_folder");
        Assert.assertTrue(findFile.isFailIfNotFound());
        Assert.assertEquals(findFile.getFileName(), "myfile.txt");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "The specified search root folder does not exist");

        findFile =  new FindFile("myfile.txt").relative("no_existent_folder");
        executionResult = findFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        Assert.assertNull(executionResult.getValue());

        Assert.assertEquals(findFile.getDescription(), "Find file named myfile.txt under no_existent_folder");
        Assert.assertFalse(findFile.isFailIfNotFound());
        Assert.assertEquals(findFile.getFileName(), "myfile.txt");
        Assert.assertNull(executionResult.getException());
    }

}
