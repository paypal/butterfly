package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Unit tests for {@link CompareXMLFiles}
 *
 * @author facarvalho
 */
public class CompareXMLFilesTest extends TransformationUtilityTestHelper {

    @Test
    public void compareXMLEqualSameFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "pom.xml"));
        CompareXMLFiles compareXML = new CompareXMLFiles("ATR").relative("pom.xml");
        TUExecutionResult executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file pom.xml to another one, return true only if their contents are equal");
    }

    @Test
    public void compareXMLEqualDifferentFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/copy_of_web.xml"));
        CompareXMLFiles compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/webapp/WEB-INF/web.xml");
        TUExecutionResult executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file /src/main/webapp/WEB-INF/web.xml to another one, return true only if their contents are equal");
    }

    @Test
    public void compareXMLDifferentFilesTest() {
        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "pom.xml"));
        CompareXMLFiles compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/webapp/WEB-INF/web.xml");
        TUExecutionResult executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file /src/main/webapp/WEB-INF/web.xml to another one, return true only if their contents are equal");
    }

    @Test
    public void compareXMLInexistentFilesTest() {
        CompareXMLFiles compareXML;
        TUExecutionResult executionResult;

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/inexistent_file.xml"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("pom.xml");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/inexistent_file.xml"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/resources/another_inexistent_file.yaml");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertTrue((Boolean) executionResult.getValue());

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "pom.xml"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/resources/another_inexistent_file.xml");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertFalse((Boolean) executionResult.getValue());
    }

    @Test
    public void errorInvalidXMLFileTest() {
        CompareXMLFiles compareXML;
        TUExecutionResult executionResult;

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "pom.xml"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/resources/application.properties");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "An exception happened when comparing the two XML files");
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file /src/main/resources/application.properties to another one, return true only if their contents are equal");

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/application.properties"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("pom.xml");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "An exception happened when comparing the two XML files");
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file pom.xml to another one, return true only if their contents are equal");

        Mockito.when(transformationContext.get("ATR")).thenReturn(new File(transformedAppFolder, "/src/main/resources/application.properties"));
        compareXML = new CompareXMLFiles().setAttribute("ATR").relative("/src/main/resources/dogs.yaml");
        executionResult = compareXML.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "An exception happened when comparing the two XML files");
        Assert.assertEquals(compareXML.getDescription(), "Compare XML file /src/main/resources/dogs.yaml to another one, return true only if their contents are equal");
    }

}
