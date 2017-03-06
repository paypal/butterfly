package com.paypal.butterfly.utilities;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;

/**
 * Helper class to write unit tests for
 * {@link com.paypal.butterfly.extensions.api.TransformationUtility}
 * sub-classes
 *
 * @author facarvalho
 */
public abstract class TransformationUtilityTestHelper {

    protected File appFolder;
    protected File transformedAppFolder;
    protected TransformationContext transformationContext = Mockito.mock(TransformationContext.class);

    @BeforeClass
    public void beforeClass() throws URISyntaxException, IOException {
        appFolder = new File(getClass().getResource("/test-app").toURI());
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws URISyntaxException, IOException {
        transformedAppFolder = new File(appFolder.getParentFile(), String.format("test-app_%s_%s_%s", method.getDeclaringClass().getSimpleName(), method.getName(), System.currentTimeMillis()));
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed app folder: %s\n", transformedAppFolder.getAbsolutePath());
    }

    protected void assertChangedFile(String relativeFilePath) throws IOException {
        Assert.assertTrue(fileHasChanged(relativeFilePath));
    }

    protected void assertNotChangedFile(String relativeFilePath) throws IOException {
        Assert.assertFalse(fileHasChanged(relativeFilePath));
    }

    protected boolean fileHasChanged(String relativeFilePath) throws IOException {
        File originalFile = new File(appFolder, relativeFilePath);
        Assert.assertTrue(originalFile.exists());

        File transformedFile = new File(transformedAppFolder, relativeFilePath);
        Assert.assertTrue(transformedFile.exists());

        return !Files.equal(originalFile, transformedFile);
    }

    protected void assertSameLineCount(String relativeFilePath) throws IOException {
        Assert.assertTrue(getLineCountDifference(relativeFilePath) == 0);
    }

    protected void assertNotSameLineCount(String relativeFilePath) throws IOException {
        Assert.assertTrue(getLineCountDifference(relativeFilePath) != 0);
    }

    protected void assertLineCount(String relativeFilePath, int difference) throws IOException {
        Assert.assertEquals(getLineCountDifference(relativeFilePath), difference);
    }

    protected int getLineCountDifference(String relativeFilePath) throws IOException {
        BufferedReader reader1 = null, reader2 = null;
        try {
            reader1 = new BufferedReader(new FileReader(new File(appFolder, relativeFilePath)));
            int originalNumberOfLines = 0;
            while (reader1.readLine() != null) originalNumberOfLines++;

            reader2 = new BufferedReader(new FileReader(new File(transformedAppFolder, relativeFilePath)));
            int numberOfLinesAfterChange = 0;
            while (reader2.readLine() != null) numberOfLinesAfterChange++;

            return numberOfLinesAfterChange - originalNumberOfLines;
        } finally {
            if (reader1 != null) reader1.close();
            if (reader2 != null) reader2.close();
        }
    }

    protected Properties getProperties(String relativeFilePath) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            Properties properties = new Properties();
            File transformedFile = new File(transformedAppFolder, relativeFilePath);
            fileInputStream = new FileInputStream(transformedFile);
            properties.load(fileInputStream);
            return properties;
        } finally {
            if (fileInputStream != null) fileInputStream.close();
        }
    }

    protected Object getObjectFromYaml(String relativeFilePath) throws IOException {
        YamlReader yamlReader = null;
        FileReader fileReader = null;
        try {
            File transformedFile = new File(transformedAppFolder, relativeFilePath);
            fileReader = new FileReader(transformedFile);
            yamlReader = new YamlReader(fileReader);
            Object object = yamlReader.read();
            return object;
        } finally {
            if (yamlReader != null) yamlReader.close();
            if (fileReader != null) fileReader.close();
        }
    }

}
