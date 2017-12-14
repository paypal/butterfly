package com.paypal.butterfly.utilities.conditions.java;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test class for {@link JavaMatch}
 *
 * @author facarvalho
 */
public class JavaMatchTest extends TransformationUtilityTestHelper {

    private  Extends ext = new Extends(Throwable.class);

    @Test
    public void singleTest() {
        JavaMatch javaMatch = new JavaMatch(ext).relative("/src/main/java/com/testapp/JavaLangSubclass.java");
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/JavaLangSubclass.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 1);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
    }

    @Test
    public void multipleTest() {
        JavaMatch javaMatch = new JavaMatch().relative("/src/main/java/com/testapp/JavaLangSubclass.java");
        javaMatch.addCondition(ext);
        javaMatch.addCondition(new AnnotatedWith(SuppressWarnings.class));
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/JavaLangSubclass.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 2);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
    }

    @Test
    public void noCompilationUnitTest() {
        JavaMatch javaMatch = new JavaMatch().relative("/src/main/java/com/testapp/NoCompilationUnits.java");
        Set<JavaCondition> conditions = new HashSet<>();
        conditions.add(ext);
        conditions.add(new AnnotatedWith(SuppressWarnings.class));
        javaMatch.setConditions(conditions);
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/NoCompilationUnits.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 2);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getWarnings().size(), 1);
        Assert.assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationUtilityException.class);
        File noCompilationUnitFile = new File(transformedAppFolder, "/src/main/java/com/testapp/NoCompilationUnits.java");
        Assert.assertEquals(executionResult.getWarnings().get(0).getMessage(), "This Java class file has no declared types: " + noCompilationUnitFile.getAbsolutePath());
    }

}
