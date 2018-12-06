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
 * Unit test for {@link PomGetParent}
 */
public class PomGetParentTest extends TransformationUtilityTestHelper {

    @Test
    public void attributeTest() throws IOException, XmlPullParserException {
        Model pomModel = getOriginalPomModel("pom.xml");
        Mockito.when(transformationContext.get("ATT")).thenReturn(pomModel);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);

        PomGetParent pomGetParent = new PomGetParent("ATT");
        assertEquals(pomGetParent.getDescription(), "Retrieve the parent Maven coordinates of specified Maven POM module");

        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "com.test:foo-parent:1.0");
    }

    @Test
    public void fileTest() {
        PomGetParent pomGetParent = new PomGetParent().relative("pom.xml");
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "com.test:foo-parent:1.0");
    }

    @Test
    public void fileAndAttributeTest() throws IOException, XmlPullParserException {
        Model pomModel = getOriginalPomModel("pom.xml");
        Mockito.when(transformationContext.get("ATT")).thenReturn(pomModel);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);

        PomGetParent pomGetParent = new PomGetParent("ATT").relative("foo.xml");
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertEquals(executionResult.getValue(), "com.test:foo-parent:1.0");
    }

    @Test
    public void noFileNoAttributeTest() {
        PomGetParent pomGetParent = new PomGetParent();
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Model transformation context attribute name nor pom file were set");
    }

    @Test
    public void nonExistentAttributeTest() {
        PomGetParent pomGetParent = new PomGetParent("NON_EXISTENT_ATT");
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NON_EXISTENT_ATT does not exist");
    }

    @Test
    public void nullAttributeTest() {
        Mockito.when(transformationContext.get("NULL_ATT")).thenReturn(null);
        Mockito.when(transformationContext.contains("NULL_ATT")).thenReturn(true);

        PomGetParent pomGetParent = new PomGetParent("NULL_ATT");
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NULL_ATT is null");
    }

    @Test
    public void notModelAttributeTest() {
        Mockito.when(transformationContext.get("NOT_MODEL_ATT")).thenReturn(new Object());
        Mockito.when(transformationContext.contains("NOT_MODEL_ATT")).thenReturn(true);

        PomGetParent pomGetParent = new PomGetParent("NOT_MODEL_ATT");
        TUExecutionResult executionResult = pomGetParent.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Transformation context attribute NOT_MODEL_ATT is not a Maven model");
    }
}
