package com.paypal.butterfly.core;

import java.io.File;

import org.testng.annotations.Test;

import com.paypal.butterfly.facade.Configuration;

import static org.testng.Assert.*;

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

        Configuration configuration1 = new ConfigurationImpl();
        configuration1.setOutputFolder(new File(System.getProperty("user.dir")));
        configuration1.setZipOutput(true);
        configuration1.setModifyOriginalFolder(false);

        Configuration configuration2 = new ConfigurationImpl().setOutputFolder(new File(System.getProperty("user.dir"))).setZipOutput(true);

        assertEquals(configuration2.getOutputFolder(), configuration1.getOutputFolder());
        assertEquals(configuration2.isModifyOriginalFolder(), configuration1.isModifyOriginalFolder());
        assertEquals(configuration2.isZipOutput(), configuration1.isZipOutput());
        assertTrue(configuration2.equals(configuration1));
        assertEquals(configuration2.hashCode(), configuration1.hashCode());
        assertFalse(configuration2.isModifyOriginalFolder());

        configuration2.setModifyOriginalFolder(true);

        //Not Equals use case
        assertFalse(configuration2.equals(configuration1));
        assertNotEquals(configuration2.hashCode(), configuration1.hashCode());
    }

    /**
     * To Test OutputFolder Setter and Getter
     */
    @Test
    public void testOutputFolder(){
        Configuration configuration = new ConfigurationImpl();
        File outputFolder = new File(System.getProperty("user.dir"));
        configuration.setOutputFolder(outputFolder);
        assertEquals(outputFolder, configuration.getOutputFolder());
    }

    /**
     *  To Test Invalid Output folder and exception it throws
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidOutputFolder(){
        Configuration configuration = new ConfigurationImpl();
        configuration.setOutputFolder(new File("TEST"));
    }

    /**
     * To Test ZipOutput Setter and Getter
     */
    @Test
    public void testZipOutput(){
        Configuration configuration = new ConfigurationImpl();
        configuration.setZipOutput(true);
        assertTrue(configuration.isZipOutput());
    }

    /**
     * To Test ModifyInPlace Setter and Getter
     */
    @Test
    public void testModifyInPlace(){
        Configuration configuration = new ConfigurationImpl();
        configuration.setModifyOriginalFolder(true);
        assertTrue(configuration.isModifyOriginalFolder());
    }

    /**
     * To Test toString method
     */
    @Test
    public void testToString(){
        Configuration configuration = new ConfigurationImpl();
        configuration.setOutputFolder(new File(System.getProperty("user.dir")));
        configuration.setZipOutput(true);
        configuration.setModifyOriginalFolder(true);
        assertNotNull(configuration.toString());
    }

}
