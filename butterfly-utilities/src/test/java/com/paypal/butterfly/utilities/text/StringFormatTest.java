package com.paypal.butterfly.utilities.text;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link StringFormat}
 *
 * @author facarvalho
 */
public class StringFormatTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        Mockito.when(transformationContext.get("object")).thenReturn("house");
        Mockito.when(transformationContext.get("color")).thenReturn("blue");

        StringFormat stringFormat = new StringFormat().setFormat("This %s is %s").setAttributeNames("object", "color");
        TUExecutionResult executionResult = stringFormat.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertEquals(executionResult.getValue(), "This house is blue");

        Assert.assertEquals(stringFormat.getFormat(), "This %s is %s");
        Assert.assertEquals(stringFormat.getAttributeNames(), new String[]{"object", "color"});
        Assert.assertEquals(stringFormat.getDescription(), "Apply transformation context attributes [object, color] to 'This %s is %s'");
    }

}
