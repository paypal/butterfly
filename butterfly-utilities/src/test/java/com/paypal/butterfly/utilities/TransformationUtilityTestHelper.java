package com.paypal.butterfly.utilities;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.custommonkey.xmlunit.XMLUnit;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Properties;

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
    protected TransformationContext transformationContext;

    @BeforeClass
    public void beforeClass() throws URISyntaxException, IOException {
        appFolder = new File(getClass().getResource("/test-app").toURI());
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws URISyntaxException, IOException {
        transformedAppFolder = new File(appFolder.getParentFile(), String.format("test-app_%s_%s_%s", method.getDeclaringClass().getSimpleName(), method.getName(), System.currentTimeMillis()));
        FileUtils.copyDirectory(appFolder, transformedAppFolder);
        System.out.printf("Transformed app folder: %s\n", transformedAppFolder.getAbsolutePath());
        transformationContext = Mockito.mock(TransformationContext.class);
    }

    /**
     * Assert that this file has been changed
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws IOException
     */
    protected void assertChangedFile(String relativeFilePath) throws IOException {
        Assert.assertTrue(fileHasChanged(relativeFilePath));
    }

    /**
     * Assert that this file has not been changed
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws IOException
     */
    protected void assertNotChangedFile(String relativeFilePath) throws IOException {
        String message = String.format("File %s was not expected to change, but it did.", relativeFilePath);
        Assert.assertFalse(fileHasChanged(relativeFilePath), message);
    }

    /**
     * Return true if the file is not identical to how it was originally
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @return true if the file is not identical to how it was originally
     * @throws IOException
     */
    protected boolean fileHasChanged(String relativeFilePath) throws IOException {
        File originalFile = new File(appFolder, relativeFilePath);
        Assert.assertTrue(originalFile.exists());

        File transformedFile = new File(transformedAppFolder, relativeFilePath);
        Assert.assertTrue(transformedFile.exists());

        return !Files.equal(originalFile, transformedFile);
    }

    /**
     * Assert that the transformed file, whether it was modified or not,
     * has the same number of lines as the original one
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws IOException
     */
    protected void assertSameLineCount(String relativeFilePath) throws IOException {
        Assert.assertTrue(getLineCountDifference(relativeFilePath) == 0);
    }

    /**
     * Assert that the transformed file was modified and now
     * does NOT have the same number of lines as the original one
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws IOException
     */
    protected void assertNotSameLineCount(String relativeFilePath) throws IOException {
        Assert.assertTrue(getLineCountDifference(relativeFilePath) != 0);
    }

    /**
     * Compare the number of lines the original and the supposedly modified
     * file have and assert that the difference between them is as stated
     * in {@code difference}. For example, 1 would mean the modified file
     * has a count of lines one larger than the original one, while -3 would
     * mean three lesser.
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws IOException
     */
    protected void assertLineCount(String relativeFilePath, int difference) throws IOException {
        Assert.assertEquals(getLineCountDifference(relativeFilePath), difference);
    }

    /**
     * Returns the difference in number of lines the supposedly transformed file
     * has compared to the original file. For example, if the original file
     * has 4 lines, but the transformed one has 3, then it returns 1.
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @return the difference in number of lines the supposedly transformed file
     * has compared to the original file
     * @throws IOException
     */
    protected int getLineCountDifference(String relativeFilePath) throws IOException {
        BufferedReader reader1 = null;
        BufferedReader reader2 = null;

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

    /**
     * Returns a properties object generated from the specified file
     *
     * @param relativeFilePath relative path to file to be used to generate the properties object
     * @return a properties object generated from the specified file
     * @throws IOException
     */
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

    /**
     * Returns a YAML object generated from the specified file
     *
     * @param relativeFilePath relative path to file to be used to generate the properties object
     * @return a YAML object generated from the specified file
     * @throws IOException
     */
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

    /**
     * Returns a {@link Model} of an original Maven pom file
     *
     * @param relativeFilePath
     * @return a {@link Model} of an original Maven pom file
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected Model getOriginalPomModel(String relativeFilePath) throws IOException, XmlPullParserException {
        return getPomModel( new File(appFolder, relativeFilePath));
    }

    /**
     * Returns a {@link Model} of a transformed Maven pom file
     *
     * @param relativeFilePath
     * @return a {@link Model} of a transformed Maven pom file
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected Model getTransformedPomModel(String relativeFilePath) throws IOException, XmlPullParserException {
        return getPomModel( new File(transformedAppFolder, relativeFilePath));
    }

    private Model getPomModel(File pomFile ) throws IOException, XmlPullParserException {
        FileInputStream inputStream = null;
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            inputStream = new FileInputStream(pomFile);
            return reader.read(inputStream);
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }

    /**
     * Assert that the specified XML file has not semantically changed,
     * although it might be identical to the original one due to format
     * changes, comments not being present, etc
     *
     * @param relativeFilePath relative path to file to be evaluated
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    protected void assertEqualsXml(String relativeFilePath) throws ParserConfigurationException, IOException, SAXException {
        File originalFile = new File(appFolder, relativeFilePath);
        File transformedFile = new File(transformedAppFolder, relativeFilePath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document originalXml = builder.parse(originalFile);
        Document transformedXml = builder.parse(transformedFile);

        originalXml.normalizeDocument();
        transformedXml.normalizeDocument();

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Assert.assertTrue(XMLUnit.compareXML(originalXml, transformedXml).similar());
    }

}
