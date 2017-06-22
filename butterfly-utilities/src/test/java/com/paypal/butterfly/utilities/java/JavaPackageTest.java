package com.paypal.butterfly.utilities.java;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
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
    public void javaLangTest() {
        JavaPackage javaPackage = new JavaPackage().relative("src/main/java/com/testapp/Application.java");
        TUExecutionResult executionResult = javaPackage.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertEquals(executionResult.getValue(), "com.testapp");
    }

}
