package com.paypal.butterfly.utilities.misc;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.script.ScriptException;

/**
 * Unit tests for {@link RunScript}
 *
 * @author facarvalho
 */
public class RunScriptTest extends TransformationUtilityTestHelper {

    @Test
    public void simpleScriptTest() {
        RunScript runScript =  new RunScript("4 + 6");
        TUExecutionResult executionResult = runScript.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), 10);
        Assert.assertEquals(runScript.getDescription(), "Executes script '4 + 6' and saves its evaluation result");
        Assert.assertEquals(runScript.getScript(), "4 + 6");
        Assert.assertEquals(runScript.getLanguage(), "js");
    }

    @Test
    public void objectsDynamicScriptTest() {
        RunScript runScript =  new RunScript().setScript("a / b").addObject("a", new Integer(27)).addObject("b", new Integer(9));
        TUExecutionResult executionResult = runScript.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), 3d);
        Assert.assertEquals(runScript.getDescription(), "Executes script 'a / b' and saves its evaluation result");
        Assert.assertEquals(runScript.getScript(), "a / b");
        Assert.assertEquals(runScript.getAttributes().size(), 0);
        Assert.assertEquals(runScript.getObjects().size(), 2);
        Assert.assertTrue(runScript.getObjects().containsKey("a"));
        Assert.assertTrue(runScript.getObjects().containsKey("b"));
        Assert.assertEquals(runScript.getObjects().get("a"), 27);
        Assert.assertEquals(runScript.getObjects().get("b"), 9);
        Assert.assertEquals(runScript.getLanguage(), "js");
    }

    @Test
    public void attributesDynamicScriptTest() {
        Mockito.when(transformationContext.get("ATRA")).thenReturn("casa");
        Mockito.when(transformationContext.get("ATRB")).thenReturn("azul");

        RunScript runScript =  new RunScript().setScript("a + ' ' + b").addAttribute("a", "ATRA").addAttribute("b", "ATRB");
        runScript.setLanguage("javascript");

        TUExecutionResult executionResult = runScript.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), "casa azul");
        Assert.assertEquals(runScript.getDescription(), "Executes script 'a + ' ' + b' and saves its evaluation result");
        Assert.assertEquals(runScript.getScript(), "a + ' ' + b");
        Assert.assertEquals(runScript.getObjects().size(), 0);
        Assert.assertEquals(runScript.getAttributes().size(), 2);
        Assert.assertTrue(runScript.getAttributes().containsKey("a"));
        Assert.assertTrue(runScript.getAttributes().containsKey("b"));
        Assert.assertEquals(runScript.getAttributes().get("a"), "ATRA");
        Assert.assertEquals(runScript.getAttributes().get("b"), "ATRB");
        Assert.assertEquals(runScript.getLanguage(), "javascript");
    }

    @Test
    public void invalidScriptTest() {
        RunScript runScript =  new RunScript("++++");
        TUExecutionResult executionResult = runScript.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(runScript.getDescription(), "Executes script '++++' and saves its evaluation result");
        Assert.assertEquals(runScript.getScript(), "++++");
        Assert.assertEquals(runScript.getObjects().size(), 0);
        Assert.assertEquals(runScript.getAttributes().size(), 0);
        Assert.assertEquals(runScript.getLanguage(), "js");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), ScriptException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "<eval>:1:2 Expected l-value but found ++\n++++\n  ^ in <eval> at line number 1 at column number 2");
    }

}
