package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for {@link LocateFile}
 *
 * @author facarvalho
 */
public class LocateFileTest extends TransformationUtilityTestHelper {

    @Test
    public void rootLocateFileTest() throws IOException {
        LocateFile locateFile =  new LocateFile();
        TUExecutionResult executionResult = locateFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        File rootFile = (File) executionResult.getValue();
        Assert.assertEquals(rootFile.getAbsolutePath(), transformedAppFolder.getAbsolutePath());
        Assert.assertEquals(locateFile.getParentLevel(), 0);
        Assert.assertEquals(locateFile.getDescription(), "Locate file root folder");
    }

    @Test
    public void locateFileTest() throws IOException {
        LocateFile locateFile =  new LocateFile().relative("pom.xml");
        TUExecutionResult executionResult = locateFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        File rootFile = (File) executionResult.getValue();
        Assert.assertEquals(rootFile, new File(transformedAppFolder, "pom.xml"));
        Assert.assertEquals(locateFile.getParentLevel(), 0);
        Assert.assertEquals(locateFile.getDescription(), "Locate file pom.xml");
    }

    @Test
    public void locateParentFileTest() throws IOException {
        LocateFile locateFile =  new LocateFile(2).relative("/src/main/resources");
        TUExecutionResult executionResult = locateFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        File file = (File) executionResult.getValue();
        Assert.assertEquals(file, new File(transformedAppFolder, "/src"));
        Assert.assertEquals(locateFile.getParentLevel(), 2);
        Assert.assertEquals(locateFile.getDescription(), "Locate file 2 levels above /src/main/resources");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Invalid parent level")
    public void invalidParentTest() throws IOException {
        new LocateFile(-2).relative("/src/main/resources");
    }

    @Test
    public void locateInvalidParentFileTest() throws IOException {
        LocateFile locateFile =  new LocateFile(2000).relative("/src/main/resources");
        TUExecutionResult executionResult = locateFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(locateFile.getParentLevel(), 2000);
        Assert.assertEquals(locateFile.getDescription(), "Locate file 2000 levels above /src/main/resources");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File to be located reached limit of files hierarchy, parent level 2000 is too deep");
    }

    @Test
    public void locateInvalidBaseFileTest() throws IOException {
        LocateFile locateFile =  new LocateFile().relative("/src/main/resources/non_existent_folder");
        TUExecutionResult executionResult = locateFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(locateFile.getParentLevel(), 0);
        Assert.assertEquals(locateFile.getDescription(), "Locate file /src/main/resources/non_existent_folder");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File to be located does not exist");
    }

}
