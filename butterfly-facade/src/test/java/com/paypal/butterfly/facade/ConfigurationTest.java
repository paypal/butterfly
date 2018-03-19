package com.paypal.butterfly.facade;

import org.testng.Assert;
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
        configuration.setModifyOriginalFolder(false);
        Configuration configuration1 = new Configuration(new File(System.getProperty("user.dir")), true);
        Assert.assertTrue(configuration.equals(configuration1));
        Assert.assertEquals(configuration.hashCode(), configuration1.hashCode());
        Assert.assertFalse(configuration.isModifyOriginalFolder());

        configuration.setModifyOriginalFolder(true);

        //Not Equals use case
        Assert.assertFalse(configuration.equals(configuration1));
        Assert.assertNotEquals(configuration.hashCode(), configuration1.hashCode());
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
     * To Test ModifyInPlace Setter and Getter
     */
    @Test
    public void testModifyInPlace(){
        Configuration configuration = new Configuration();
        configuration.setModifyOriginalFolder(true);
        Assert.assertTrue(configuration.isModifyOriginalFolder());
    }

    /**
     * To Test toString method
     */
    @Test
    public void testToString(){
        Configuration configuration = new Configuration();
        configuration.setOutputFolder(new File(System.getProperty("user.dir")));
        configuration.setZipOutput(true);
        configuration.setModifyOriginalFolder(true);
        Assert.assertNotNull(configuration.toString());
    }

}
