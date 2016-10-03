package com.paypal.butterfly.facade;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * This is unit test class for Configuration
 * Created by akumar46 on 9/14/2016.
 */
public class ConfigurationTest {

    /**
     * To Test equals and hashCode methods
     */
    @Test
    public void testEqualsAndHashCode(){
        //Equals use case
        Configuration configuration = new Configuration();
        configuration.setOutputFolder(new File(System.getProperty("user.dir")));
        configuration.setZipOutput(true);
        Configuration configuration1 = new Configuration(new File(System.getProperty("user.dir")), true);
        Assert.assertTrue(configuration.equals(configuration1));
        Assert.assertEquals(configuration.hashCode(), configuration1.hashCode());
        //Not Equals use case
        configuration.setZipOutput(false);
        Assert.assertFalse(configuration.equals(configuration1));
    }


    /**
     * To Test OutputFolder Setter and Getter
     */
    @Test
    public void testOutputFolder(){
        Configuration configuration = new Configuration();
        File outputFolder = new File(System.getProperty("user.dir"));
        configuration.setOutputFolder(outputFolder);
        Assert.assertEquals(outputFolder, configuration.getOutputFolder());
    }

    /**
     *  To Test Invalid Output folder and exception it throws
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidOutputFolder(){
        Configuration configuration = new Configuration();
        configuration.setOutputFolder(new File("TEST"));
    }

    /**
     * To Test ZipOutput Setter and Getter
     */
    @Test
    public void testZipOutput(){
        Configuration configuration = new Configuration();
        configuration.setZipOutput(true);
        Assert.assertTrue(configuration.isZipOutput());
    }

    /**
     * To Test toString method
     */
    @Test
    public void testToString(){
        Configuration configuration = new Configuration();
        configuration.setOutputFolder(new File(System.getProperty("user.dir")));
        configuration.setZipOutput(true);
        Assert.assertNotNull(configuration.toString());
    }

}
