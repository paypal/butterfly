package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for {@link RemoveLine}
 *
 * @author facarvalho
 */
public class RemoveLineTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*import java.util.Properties;.*)").relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/java/com/testapp/Application.java");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        RemoveLine removeLine = new RemoveLine(1798).relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/java/com/testapp/Application.java");
    }

    @Test
    public void removeLineNumberTest() throws IOException {
        RemoveLine removeLine = new RemoveLine(2).relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("foo"), "foov");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void removeFirstRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*foo.*)").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -1);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
    }

    @Test
    public void removeAllRegexTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*foo.*)").relative("/src/main/resources/application.properties").setFirstOnly(false);
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -2);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 1);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
    }

    @Test
    public void removeAndAddTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(Main-Class:.*)").relative("/src/main/resources/MANIFEST.MF");
        TOExecutionResult removeLineExecutionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(removeLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        AddLine addLine = new AddLine().setNewLine("Main-Class: com.mytestapp.Application").relative("/src/main/resources/MANIFEST.MF");
        TOExecutionResult addLineExecutionResult = addLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(addLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/MANIFEST.MF");
        assertSameLineCount("/src/main/resources/MANIFEST.MF");
    }

    @Test
    public void removeAndInsertTest() throws IOException {
        RemoveLine removeLine = new RemoveLine().setRegex("(package com.testapp;)").relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult removeLineExecutionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(removeLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        InsertLine insertLine = new InsertLine().setNewLine("package com.mytestapp;").setLineNumber(1).relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult insertLineExecutionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(insertLineExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/java/com/testapp/Application.java");
        assertSameLineCount("/src/main/java/com/testapp/Application.java");
    }

    @Test
    public void fileDoesNotExistTest() {
        RemoveLine removeLine = new RemoveLine(3).relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertEquals(executionResult.getException(), null);
    }

}
