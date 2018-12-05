package com.paypal.butterfly.utilities.pom;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit test for {@link PomGetPackaging}
 */
public class PomGetPackagingTest extends TransformationUtilityTestHelper {

    @Test
    public void attributeTest() throws IOException, XmlPullParserException {
        Model pomModel = getOriginalPomModel("pom.xml");
        Mockito.when(transformationContext.get("ATT")).thenReturn(pomModel);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);

        PomGetPackaging pomGetPackaging = new PomGetPackaging("ATT");
        assertEquals(pomGetPackaging.getDescription(), "Retrieve the packaging of specified Maven POM module");

        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "jar");
    }

    @Test
    public void fileTest() {
        PomGetPackaging pomGetPackaging = new PomGetPackaging().relative("pom.xml");
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "jar");
    }

    @Test
    public void fileAndAttributeTest() throws IOException, XmlPullParserException {
        Model pomModel = getOriginalPomModel("pom.xml");
        Mockito.when(transformationContext.get("ATT")).thenReturn(pomModel);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);

        PomGetPackaging pomGetPackaging = new PomGetPackaging("ATT").relative("foo.xml");
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "jar");
    }

    @Test
    public void noFileNoAttributeTest() {
        PomGetPackaging pomGetPackaging = new PomGetPackaging();
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Model transformation context attribute name nor pom file were set");
    }

    @Test
    public void nonExistentAttributeTest() {
        PomGetPackaging pomGetPackaging = new PomGetPackaging("NON_EXISTENT_ATT");
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NON_EXISTENT_ATT does not exist");
    }

    @Test
    public void nullAttributeTest() {
        Mockito.when(transformationContext.get("NULL_ATT")).thenReturn(null);
        Mockito.when(transformationContext.contains("NULL_ATT")).thenReturn(true);

        PomGetPackaging pomGetPackaging = new PomGetPackaging("NULL_ATT");
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NULL_ATT is null");
    }

    @Test
    public void notModelAttributeTest() {
        Mockito.when(transformationContext.get("NOT_MODEL_ATT")).thenReturn(new Object());
        Mockito.when(transformationContext.contains("NOT_MODEL_ATT")).thenReturn(true);

        PomGetPackaging pomGetPackaging = new PomGetPackaging("NOT_MODEL_ATT");
        TUExecutionResult executionResult = pomGetPackaging.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NOT_MODEL_ATT is not a Maven model");
    }
}
