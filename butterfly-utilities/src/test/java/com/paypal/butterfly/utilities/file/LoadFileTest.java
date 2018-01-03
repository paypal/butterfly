package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Unit tests for {@link LoadFile}
 *
 * @author facarvalho
 */
public class LoadFileTest extends TransformationUtilityTestHelper {

    @Test
    public void loadFileTest() throws IOException, URISyntaxException {
        LoadFile loadFile =  new LoadFile("stylesheet.css");
        TUExecutionResult executionResult = loadFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertEquals(loadFile.getResource(), "stylesheet.css");
        Assert.assertNotNull(executionResult.getValue());
        File loadedFile = (File) executionResult.getValue();
        Assert.assertTrue(loadedFile.getName().endsWith("stylesheet.css"));
        Assert.assertTrue(loadedFile.exists());
        Assert.assertTrue(loadedFile.isFile());
        Assert.assertEquals(loadFile.getDescription(), "Load resource stylesheet.css and writes it to a temporary file");
        Assert.assertNull(executionResult.getException());
        File originalFile = new File(getClass().getResource("/stylesheet.css").toURI());
        Assert.assertTrue(FileUtils.contentEquals(originalFile, loadedFile));
    }

    @Test
    public void fileNotFoundTest() {
        LoadFile loadFile =  new LoadFile().setResource("non_existent_file.txt");
        TUExecutionResult executionResult = loadFile.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertEquals(loadFile.getResource(), "non_existent_file.txt");
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(loadFile.getDescription(), "Load resource non_existent_file.txt and writes it to a temporary file");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Resource non_existent_file.txt could not be found in the classpath");
    }

}
