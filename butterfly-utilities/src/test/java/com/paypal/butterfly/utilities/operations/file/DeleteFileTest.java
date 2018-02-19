package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class DeleteFileTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        File pomFile = new File(transformedAppFolder, "pom.xml");
        assertTrue(pomFile.exists());
        DeleteFile deleteFile = new DeleteFile().relative("pom.xml");
        TOExecutionResult executionResult = deleteFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertFalse(pomFile.exists());
        assertEquals(deleteFile.getDescription(), "Delete file pom.xml");
        assertEquals(executionResult.getDetails(), "File 'pom.xml' has been removed");
    }

    @Test
    public void fileDoesNotExistTest() {
        File pomFile = new File(transformedAppFolder, "arquivo");
        assertFalse(pomFile.exists());
        DeleteFile deleteFile = new DeleteFile().relative("arquivo");
        TOExecutionResult executionResult = deleteFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertFalse(pomFile.exists());
        assertEquals(deleteFile.getDescription(), "Delete file arquivo");
        assertEquals(executionResult.getDetails(), "File 'arquivo' was not removed because it does not exist");
    }

    @Test
    public void fileCanNotBeResolvedTest() {
        DeleteFile deleteFile = new DeleteFile().absolute("ARG");
        TOExecutionResult executionResult = deleteFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(deleteFile.getDescription(), "Delete file null");
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "No file has been removed because the file path has not been resolved");
    }

}
