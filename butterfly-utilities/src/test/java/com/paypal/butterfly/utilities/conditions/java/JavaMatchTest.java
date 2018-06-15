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

    @Test
    public void commentedOutShortClassTest() {
        JavaMatch javaMatch = new JavaMatch().relative("/src/main/java/com/testapp/CommentedOutShort.java");

        Set<JavaCondition> conditions = new HashSet<>();
        conditions.add(ext);
        conditions.add(new AnnotatedWith(SuppressWarnings.class));

        javaMatch.setConditions(conditions);
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);

        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/CommentedOutShort.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 2);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getWarnings().size(), 1);
        Assert.assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationUtilityException.class);

        File commentedOutShortFile = new File(transformedAppFolder, "/src/main/java/com/testapp/CommentedOutShort.java");
        Assert.assertEquals(executionResult.getWarnings().get(0).getMessage(), "This Java class file has no declared types: " + commentedOutShortFile.getAbsolutePath());
    }

//  This test takes over two minutes to run
//  It used to fail before this issue was solved: https://github.com/paypal/butterfly/issues/89
//  The solution was simple, upgrading javaparser did it (the class where the root cause was doesn't exist anymore)
//  Uncomment this Test it if this same problem occurs again to help with debugging
//  The root cause was a StackOverflowError thrown com.github.javaparser.ASTParserTokenManager.CommonTokenAction, which is a recursive method in javaparser 3.0.1
//    @Test
    public void commentedOutLongClassTest() {
        JavaMatch javaMatch = new JavaMatch().relative("/src/main/java/com/testapp/CommentedOutLong.java");

        Set<JavaCondition> conditions = new HashSet<>();
        conditions.add(ext);
        conditions.add(new AnnotatedWith(SuppressWarnings.class));

        javaMatch.setConditions(conditions);
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);

        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertFalse((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/CommentedOutLong.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 2);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getWarnings().size(), 1);
        Assert.assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationUtilityException.class);

        File commentedOutLongFile = new File(transformedAppFolder, "/src/main/java/com/testapp/CommentedOutLong.java");
        Assert.assertEquals(executionResult.getWarnings().get(0).getMessage(), "This Java class file has no declared types: " + commentedOutLongFile.getAbsolutePath());
    }

    @Test
    public void longClassTest() {
        JavaMatch javaMatch = new JavaMatch(ext).relative("/src/main/java/com/testapp/JavaLangSubclassLong.java");
        TUExecutionResult executionResult = javaMatch.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertTrue((Boolean) executionResult.getValue());
        Assert.assertEquals(javaMatch.getDescription(), "Check if Java class in '/src/main/java/com/testapp/JavaLangSubclassLong.java' matches all specified criteria");
        Assert.assertEquals(javaMatch.getConditions().size(), 1);
        Assert.assertTrue(javaMatch.getConditions().contains(ext));
    }

}
