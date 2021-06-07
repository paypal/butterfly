package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link MoveDirectory}
 *
 * @author facarvalho
 */
public class MoveDirectoryTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        File fromDir = new File(transformedAppFolder, "blah");
        File toDir = new File(transformedAppFolder, "/src/main/resources/blah");

        assertTrue(fromDir.exists());
        assertTrue(fromDir.isDirectory());
        assertFalse(toDir.exists());

        int fromDirChildrenCount = fromDir.listFiles().length;

        MoveDirectory moveDirectory = new MoveDirectory().relative("blah").setToRelative("src/main/resources/blah");
        TOExecutionResult executionResult = moveDirectory.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(moveDirectory.getDescription(), "Move a directory from blah to src/main/resources/blah");
        assertEquals(executionResult.getDetails(), "Directory '/blah' has been moved to '/src/main/resources/blah'");

        // Since the actual move operation is delegated to Apache Commons FileUtils.moveDirectory, the actual move
        // operation won't be tested too extensively here

        assertFalse(fromDir.exists());
        assertTrue(toDir.exists());
        assertTrue(toDir.isDirectory());
        assertEquals(fromDirChildrenCount, toDir.listFiles().length);
    }

    @Test
    public void nonExistentFromFolderTest() {
        Mockito.when(transformationContext.get("ATT")).thenReturn(new File(transformedAppFolder, "/src/main/resources"));
        MoveDirectory moveDirectory = new MoveDirectory().relative("nonExistentFolder").setToAbsolute("ATT", "blah");
        TOExecutionResult executionResult = moveDirectory.execution(transformedAppFolder, transformationContext);

        assertEquals(moveDirectory.getToAbsoluteAttribute(), "ATT");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Directory could not be moved");
        assertEquals(executionResult.getException().getCause().getMessage(), "Source '" + new File(transformedAppFolder, "nonExistentFolder").getAbsolutePath() + "' does not exist");
        assertEquals(moveDirectory.getDescription(), "Move a directory from nonExistentFolder to the location defined by transformation context attribute ATT");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void existentToFolderTest() {
        MoveDirectory moveDirectory = new MoveDirectory().relative("blah").setToRelative("src/main/resources");
        TOExecutionResult executionResult = moveDirectory.execution(transformedAppFolder, transformationContext);

        assertEquals(moveDirectory.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Directory could not be moved");
        assertEquals(executionResult.getException().getCause().getMessage(), "File element in parameter 'destDir' already exists: '" + new File(transformedAppFolder, "src/main/resources").getAbsolutePath() + "'");
        assertEquals(moveDirectory.getDescription(), "Move a directory from blah to src/main/resources");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void nonDirectoryTest() {
        MoveDirectory moveDirectory = new MoveDirectory().relative("pom.xml").setToRelative("src/main/resources");
        TOExecutionResult executionResult = moveDirectory.execution(transformedAppFolder, transformationContext);

        assertEquals(moveDirectory.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Directory could not be moved");
        assertEquals(executionResult.getException().getCause().getMessage(), "Parameter 'srcDir' is not a directory: '" + new File(transformedAppFolder, "pom.xml").getAbsolutePath() + "'");
        assertEquals(moveDirectory.getDescription(), "Move a directory from pom.xml to src/main/resources");
        assertNull(executionResult.getDetails());
    }

}
