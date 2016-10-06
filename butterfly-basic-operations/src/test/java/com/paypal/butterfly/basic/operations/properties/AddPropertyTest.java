package com.paypal.butterfly.basic.operations.properties;


import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Properties;

/**
 * Unit Tests for Add Property operation.
 * Created by akumar46 on 9/30/2016.
 */
public class AddPropertyTest {

    /**
     * Just to Clear the properties(Not a Unit Test) in add-test.properties (If Any) file before executing Add, Set and Remove Test cases.
     * @throws IOException
     */
    @BeforeClass
    public void setUp() throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream("/add-test.properties");
            Properties properties = readPropertiesFile(inputStream);
            properties.clear();
            outputStream = new FileOutputStream(this.getClass().getResource("/add-test.properties").getFile());
            properties.store(outputStream, null);
        } catch (IOException e) {
            //Do Nothing
        } finally {
            try {
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException e) {
                    //Do Nothing
                }
            } finally {
                if(outputStream != null) try {
                    outputStream.close();
                } catch (IOException e) {
                    //Do Nothing
                }
            }
        }
    }

    /**
     * To Test Add Property Operation
     * @throws IOException
     */
    @Test
    public void testAddAndSetProperty() throws IOException {
        InputStream inputStream = null;
        try {
            AddProperty addProperty = new AddProperty();
            //Add Property
            TOExecutionResult toAddExecutionResult = addProperty.setPropertyName("color").setPropertyValue("green").relative("").execution(new File(this.getClass().getResource("/add-test.properties").getFile()),null);
            inputStream = this.getClass().getResourceAsStream("/add-test.properties");
            Properties properties = readPropertiesFile(inputStream);
            if(inputStream != null) {
                inputStream.close();
            }
            Assert.assertTrue(properties.containsKey("color"));
            Assert.assertEquals(properties.getProperty("color"), "green");
            Assert.assertEquals(toAddExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);
            //SetProperty ( Replace Property )
            TOExecutionResult toSetExecutionResult = addProperty.setPropertyName("color").setPropertyValue("blue").relative("").execution(new File(this.getClass().getResource("/add-test.properties").getFile()),null);
            //ReLoad the property file to compare
            inputStream = this.getClass().getResourceAsStream("/add-test.properties");
            Properties prop = readPropertiesFile(inputStream);
            if(inputStream != null) {
                inputStream.close();
            }
            Assert.assertEquals("blue", prop.getProperty("color"));
            Assert.assertEquals(toSetExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);
        } catch (IOException e) {
            Assert.fail();
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * To Test Add Property When No Property File Exists
     */
    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    @Test
    public void testAddPropertyWhenNoFileExists() {
        TOExecutionResult toExecResult = new AddProperty().setPropertyName("color").setPropertyValue("green").relative("").execution(new File("/add-test-dummy.properties"), null);
        Assert.assertEquals(toExecResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(toExecResult.getException().getClass(), FileNotFoundException.class);
    }

    /**
     * Helper method to read properties file. Haven't added this elsewhere as it is only for UT.
     * @param inputStream
     * @return Properties
     * @throws TransformationDefinitionException
     * @throws IOException
     */
    private Properties readPropertiesFile(InputStream inputStream) throws TransformationDefinitionException, IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }



}
