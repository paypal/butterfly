package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

/**
 * Unit test for {@link AnnotatedWith}
 *
 * @author facarvalho
 */
public class AnnotatedWithTest {

    private CompilationUnit compilationUnit;

    @BeforeClass
    public void beforeClass() throws ParseException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/test-app/src/main/java/com/testapp/Application.java");
        compilationUnit = JavaParser.parse(resourceAsStream);
    }

    @Test
    public void javaLangTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith(SuppressWarnings.class);
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void simpleNameTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith(ComponentScan.class);
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void fqdnTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith(EnableAutoConfiguration.class);
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void samePackageTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("com.testapp.MyAnnotation");
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void negativeTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith(Component.class);
        Assert.assertFalse(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void samePackageNegativeTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("com.differentpackage.MyOtherAnnotation");
        Assert.assertFalse(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void javaLangStringTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("java.lang.SuppressWarnings");
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void simpleNameStringTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("org.springframework.context.annotation.ComponentScan");
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void fqdnStringTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("org.springframework.boot.autoconfigure.EnableAutoConfiguration");
        Assert.assertTrue(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void negativeStringTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith("org.springframework.stereotype.Component");
        Assert.assertFalse(annotatedWith.isNegate());
        Assert.assertFalse(annotatedWith.evaluate(compilationUnit));
    }

    @Test
    public void negateTest() throws ParseException {
        AnnotatedWith annotatedWith = new AnnotatedWith(SuppressWarnings.class).setNegate(true);
        Assert.assertTrue(annotatedWith.isNegate());
        Assert.assertFalse(annotatedWith.evaluate(compilationUnit));
    }

}
