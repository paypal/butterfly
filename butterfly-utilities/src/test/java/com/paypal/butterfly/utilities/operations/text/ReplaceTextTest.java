package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Unit test for {@link ReplaceText}
 *
 * @author facarvalho
 */
public class ReplaceTextTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpTest() throws IOException {
        ReplaceText replaceText = new ReplaceText().setRegex("(\\$\\{packageName\\})").setReplacement("com.testapp").relative("src/main/webapp/WEB-INF/web.xml");
        assertEquals(replaceText.getRegex(), "(\\$\\{packageName\\})");
        assertEquals(replaceText.getReplacement(), "com.testapp");
        assertTrue(replaceText.isFirstOnly());
        assertEquals(replaceText.getDescription(), "Replace text in src/main/webapp/WEB-INF/web.xml based on regular expression (\\$\\{packageName\\})");

        TOExecutionResult executionResult = replaceText.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/webapp/WEB-INF/web.xml");
    }

    @Test
    public void successFirstOnlyTest() throws IOException {
        ReplaceText replaceText = new ReplaceText("foo").setReplacement("zoo").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = replaceText.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertSameLineCount("/src/main/resources/application.properties");

        Properties properties = getProperties("/src/main/resources/application.properties");

        assertEquals(properties.size(), 3);
        assertEquals(properties.getProperty("bar"), "barv");
        assertEquals(properties.getProperty("zoo"), "zoov");
        assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void successAllTest() throws IOException {
        ReplaceText replaceText = new ReplaceText("foo", "zoo").relative("/src/main/resources/application.properties").setFirstOnly(false);
        assertFalse(replaceText.isFirstOnly());

        TOExecutionResult executionResult = replaceText.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertSameLineCount("/src/main/resources/application.properties");

        Properties properties = getProperties("/src/main/resources/application.properties");

        assertEquals(properties.size(), 3);
        assertEquals(properties.getProperty("bar"), "barv");
        assertEquals(properties.getProperty("zoo"), "zoov");
        assertEquals(properties.getProperty("zoozoo"), "zoozoov");
    }

    @Test
    public void fileDoesNotExistTest() {
        ReplaceText replaceText = new ReplaceText("foo").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = replaceText.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Could not replace text");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "/src/main/resources/application_zeta.properties").getAbsolutePath() + " (No such file or directory)");
    }

}
