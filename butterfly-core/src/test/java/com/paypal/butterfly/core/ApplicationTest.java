package com.paypal.butterfly.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * ApplicationTest
 *
 * Created by vkuncham on 11/1/2016.
 */
public class ApplicationTest {

    private Application application = new Application(
            new File(this.getClass().getClassLoader().getResource("test-app-2").getFile()));

    @Test
    public void testGetFolder() {
        Assert.assertEquals(application.getFolder(),
                (new File(this.getClass().getClassLoader().getResource("test-app-2").getFile())));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp= "Invalid application folder test_transformed"
    )
    public void testSetInValidFolder() {
        application.setFolder(new File("test_transformed"));
    }


    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp= "Invalid application folder null"
        )
    public void testSetFolderAsNull() {
        application.setFolder(null);
    }


    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp= "Invalid application folder .*"
    )
    public void testSetFolderWithNoDirectory() {
        application.setFolder(new File(
                this.getClass().getClassLoader().getResource("test-app-2/placeHolder.txt").getFile()));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(application.toString(),
                (new File(this.getClass().getClassLoader().getResource("test-app-2").getFile()).getAbsolutePath()));
    }

}
