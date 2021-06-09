package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link RenameFile}
 *
 * @author facarvalho
 */
public class RenameFileTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws IOException {
        File originalFile = new File(transformedAppFolder, "foo.xml");

        assertTrue(originalFile.exists());
        assertTrue(originalFile.isFile());

        // Saving original file as a temp file to have its content compared later
        File tempOriginalFile = File.createTempFile("butterfly-test-file", null);
        FileUtils.copyFile(originalFile, tempOriginalFile);

        RenameFile renameFile = new RenameFile("bar.xml").relative("foo.xml");
        TOExecutionResult executionResult = renameFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(renameFile.getDescription(), "Rename file foo.xml to bar.xml");
        assertEquals(executionResult.getDetails(), "File 'foo.xml' has been renamed to 'bar.xml'");

        File renamedFile = new File(transformedAppFolder, "bar.xml");
        assertFalse(originalFile.exists());
        assertTrue(renamedFile.exists());
        assertTrue(FileUtils.contentEquals(tempOriginalFile, renamedFile));

        tempOriginalFile.delete();
    }

    @Test
    public void nonExistentFileTest() {
        RenameFile renameFile = new RenameFile().setNewName("bar.xml").relative("boo.xml");
        TOExecutionResult executionResult = renameFile.execution(transformedAppFolder, transformationContext);

        assertEquals(renameFile.getNewName(), "bar.xml");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be renamed");
        assertEquals(executionResult.getException().getCause().getMessage(), "Source '" + new File(transformedAppFolder, "boo.xml").getAbsolutePath() + "' does not exist");
        assertEquals(renameFile.getDescription(), "Rename file boo.xml to bar.xml");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void nonFileTest() {
        RenameFile renameFile = new RenameFile("boo").relative("blah");
        TOExecutionResult executionResult = renameFile.execution(transformedAppFolder, transformationContext);

        assertEquals(renameFile.getNewName(), "boo");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be renamed");
        assertEquals(executionResult.getException().getCause().getMessage(), "Parameter 'srcFile' is not a file: " + new File(transformedAppFolder, "blah").getAbsolutePath());
        assertEquals(renameFile.getDescription(), "Rename file blah to boo");
        assertNull(executionResult.getDetails());
    }

}
