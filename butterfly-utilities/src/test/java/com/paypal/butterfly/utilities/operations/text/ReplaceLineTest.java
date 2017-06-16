package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for {@link ReplaceLine}
 *
 * @author facarvalho
 */
public class ReplaceLineTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpRegexTest() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine("(.*import java.util.Properties;.*)", "import java.io.IOException;").relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/java/com/testapp/Application.java");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine(1798, "import java.io.IOException;").relative("/src/main/java/com/testapp/Application.java");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/java/com/testapp/Application.java");
    }

    @Test
    public void replaceLineNumber1Test() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine(1, "zog=zogv").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 0);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 3);
        Assert.assertEquals(properties.getProperty("zog"), "zogv");
        Assert.assertEquals(properties.getProperty("bar"), "barv");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void replaceLineNumber2Test() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine(2, "bar=barv-changed").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 0);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 3);
        Assert.assertEquals(properties.getProperty("foo"), "foov");
        Assert.assertEquals(properties.getProperty("bar"), "barv-changed");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void replaceFirstRegexTest() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine("(.*foo.*)", "zoo=zoov").relative("/src/main/resources/application.properties");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 0);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 3);
        Assert.assertEquals(properties.getProperty("zoo"), "zoov");
        Assert.assertEquals(properties.getProperty("bar"), "barv");
        Assert.assertEquals(properties.getProperty("foofoo"), "foofoov");
    }

    @Test
    public void replaceAllRegexTest() throws IOException {
        ReplaceLine replaceLine = new ReplaceLine("(.*foo.*)", "zoo=zoov").relative("/src/main/resources/application.properties").setFirstOnly(false);
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        // Still three lines
        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", 0);

        Properties properties = getProperties("/src/main/resources/application.properties");

        // But only two properties, since now there are duplicated lines
        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("zoo"), "zoov");
        Assert.assertEquals(properties.getProperty("bar"), "barv");
    }

    @Test
    public void fileDoesNotExistTest() {
        ReplaceLine replaceLine = new ReplaceLine(3, "blah").relative("/src/main/resources/application_zeta.properties");
        TOExecutionResult executionResult = replaceLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertEquals(executionResult.getException(), null);
    }

}
