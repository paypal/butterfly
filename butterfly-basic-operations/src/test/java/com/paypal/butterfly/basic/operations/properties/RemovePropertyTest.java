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
 * Unit Test for Remove Property Operation
 * Created by akumar46 on 10/6/2016.
 */
public class RemovePropertyTest {

    /**
     * Just to add a property(Not a Unit Test) in remove-test.properties file before executing Remove Test case to make sure existing property gets removed.
     * @throws IOException
     */
    @BeforeClass
    public void setUp() throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream("/remove-test.properties");
            Properties properties = readPropertiesFile(inputStream);
            properties.setProperty("color","green");
            outputStream = new FileOutputStream(this.getClass().getResource("/remove-test.properties").getFile());
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
     * To Test Remove Property Feature
     * @throws IOException
     */
    @Test
    public void testRemoveProperty() throws IOException {
        InputStream inputStream = null;
        try {
            TOExecutionResult toExecResult = new RemoveProperty("color").relative("").execution(new File(this.getClass().getResource("/remove-test.properties").getFile()),null);
            inputStream = this.getClass().getResourceAsStream("/remove-test.properties");
            Properties prop = readPropertiesFile(inputStream);
            if(inputStream != null) {
                inputStream.close();
            }
            Assert.assertFalse(prop.containsKey("color"));
            Assert.assertEquals(toExecResult.getType(), TOExecutionResult.Type.SUCCESS);
            //Try to Remove the Property which doesn't exist
            TOExecutionResult toExecutionResult = new RemoveProperty("color").relative("").execution(new File(this.getClass().getResource("/remove-test.properties").getFile()),null);
            Assert.assertNotNull(toExecutionResult);
            Assert.assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.WARNING);
        } catch (IOException e) {
            Assert.fail();
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * To Test Remove Property When Property File Doesn't Exists
     */
    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    @Test
    public void testRemovePropertyWhenNoFileExists() {
        TOExecutionResult toExecResult = new RemoveProperty("color").relative("").execution(new File("/remove-test-dummy.properties"), null);
        Assert.assertEquals(toExecResult.getType(), TOExecutionResult.Type.NO_OP);
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
