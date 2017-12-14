package com.paypal.butterfly.utilities.java;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link JavaPackage}
 *
 * @author facarvalho
 */
public class JavaPackageTest extends TransformationUtilityTestHelper {

    @Test
    public void simpleTest() {
        JavaPackage javaPackage = new JavaPackage().relative("src/main/java/com/testapp/Application.java");
        TUExecutionResult executionResult = javaPackage.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertEquals(executionResult.getValue(), "com.testapp");
        Assert.assertEquals(javaPackage.getDescription(), "Retrieve the package of a Java class file src/main/java/com/testapp/Application.java");
    }

    @Test
    public void emptyJavaFileTest() {
        JavaPackage javaPackage = new JavaPackage().relative("src/main/java/com/testapp/NoCompilationUnits.java");
        TUExecutionResult executionResult = javaPackage.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "An error happened when trying to read and parse the specified Java file /src/main/java/com/testapp/NoCompilationUnits.java");
        Assert.assertEquals(javaPackage.getDescription(), "Retrieve the package of a Java class file src/main/java/com/testapp/NoCompilationUnits.java");
    }

}
