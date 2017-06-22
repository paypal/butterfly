package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Unit test class for {@link Extends}
 *
 * @author facarvalho
 */
public class ExtendsTest {

    @Test
    public void javaLangTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/JavaLangSubclass.java", Throwable.class, true);
    }

    @Test
    public void simpleNameTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SimpleNameSubclass.java", Logger.class, true);
    }

    @Test
    public void fqdnTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/FqdnSubclass.java", Logger.class, true);
    }

    @Test
    public void samePackageTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SamePackageSubclass.java", "com.testapp.SamePackageSuperclass", true);
    }

    @Test
    public void negativeTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SimpleNameSubclass.java", Exception.class, false);
    }

    @Test
    public void samePackageNegativeTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SamePackageSubclass.java", "com.testapp.SamePackageOtherSuperclass", false);
    }

    @Test
    public void javaLangStringTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/JavaLangSubclass.java", "java.lang.Throwable", true);
    }

    @Test
    public void simpleNameStringTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SimpleNameSubclass.java", "java.util.logging.Logger", true);
    }

    @Test
    public void fqdnStringTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/FqdnSubclass.java", "java.util.logging.Logger", true);
    }

    @Test
    public void negativeStringTest() throws ParseException {
        test("/test-app/src/main/java/com/testapp/SimpleNameSubclass.java", "java.util.logging.Exception", false);
    }

    @Test
    public void negateTest() throws ParseException {
        String resourceName = "/test-app/src/main/java/com/testapp/JavaLangSubclass.java";
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        CompilationUnit compilationUnit = JavaParser.parse(resourceAsStream);
        Extends extendsObj = new Extends(Throwable.class).setNegate(true);
        Assert.assertFalse(extendsObj.evaluate(compilationUnit));
    }

    private void test(String resourceName, Class superClass, boolean expects) throws ParseException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        CompilationUnit compilationUnit = JavaParser.parse(resourceAsStream);
        Extends extendsObj = new Extends(superClass);
        Assert.assertEquals(extendsObj.evaluate(compilationUnit), expects);
    }

    private void test(String resourceName, String superClassName, boolean expects) throws ParseException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        CompilationUnit compilationUnit = JavaParser.parse(resourceAsStream);
        Extends extendsObj = new Extends(superClassName);
        Assert.assertEquals(extendsObj.evaluate(compilationUnit), expects);
    }

}
