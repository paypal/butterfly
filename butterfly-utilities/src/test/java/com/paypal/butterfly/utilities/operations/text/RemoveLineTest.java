package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import com.paypal.butterfly.utilities.operations.properties.RemoveProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Unit test for {@link RemoveLine}
 *
 * @author facarvalho
 */
public class RemoveLineTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*import java.util.Properties;.*)").relative("Application.java");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("Application.java");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        RemoveLine removeLine = new RemoveLine(1798).relative("Application.java");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("Application.java");
    }

    @Test
    public void removeLineNumberTest() throws IOException {
        RemoveLine removeLine = new RemoveLine(2).relative("application.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("application.properties");
        assertLineCount("application.properties", -1);

        Properties properties = getProperties("application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("foo"), "foov");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void removeFirstRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*foo.*)").relative("application.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("application.properties");
        assertLineCount("application.properties", -1);

        Properties properties = getProperties("application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
    }

    @Test
    public void removeAllRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*foo.*)").relative("application.properties").setFirstOnly(false);
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("application.properties");
        assertLineCount("application.properties", -2);

        Properties properties = getProperties("application.properties");

        Assert.assertEquals(properties.size(), 1);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
    }

    @Test
    public void removeAndAddTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(Main-Class:.*)").relative("MANIFEST.MF");
        TOExecutionResult removeLineExecutionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(removeLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        AddLine addLine = new AddLine().setNewLine("Main-Class: com.mytestapp.Application").relative("MANIFEST.MF");
        TOExecutionResult addLineExecutionResult = addLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(addLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("MANIFEST.MF");
        assertSameLineCount("MANIFEST.MF");
    }

    @Test
    public void removeAndInsertTest() throws IOException {
        RemoveLine removeLine = new RemoveLine().setRegex("(package com.testapp;)").relative("Application.java");
        TOExecutionResult removeLineExecutionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(removeLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        InsertLine insertLine = new InsertLine().setNewLine("package com.mytestapp;").setLineNumber(1).relative("Application.java");
        TOExecutionResult insertLineExecutionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(insertLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("Application.java");
        assertSameLineCount("Application.java");
    }

    @Test
    public void fileDoesNotExistTest() {
        RemoveLine removeLine = new RemoveLine(3).relative("application_zeta.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertEquals(executionResult.getException(), null);
    }

}
