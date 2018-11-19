package com.paypal.butterfly.test;

import java.io.File;

import com.google.common.io.Files;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MyTest {

    @Test
    public void test1() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

    @Test
    public void test2() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

    @Test
    public void test3() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

    @Test
    public void test4() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

    @Test
    public void test5() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

    @Test
    public void test6() {
        assertTrue(new File(Files.createTempDir(), "bla").mkdir());
    }

}
