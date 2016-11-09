package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This utility condition compares two XML files and returns true only
 * if their contents are equal. Attribute orders, comments and white
 * spaces are ignored during the comparison.
 * </br>
 * The first file is the one specified by {@link #relative(String)}
 * or {@link #absolute(String)}, while the second one is specified
 * as a String containing the name of the transformation context
 * attribute that holds the file.
 *
 * @author facarvalho
 */
public class CompareXMLFiles extends AbstractCompareFiles<CompareXMLFiles> {

    @Override
    protected boolean compare(File baselineFile, File comparisonFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            factory.setCoalescing(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document baselineXml = builder.parse(baselineFile);
            Document comparisonXml = builder.parse(comparisonFile);

            baselineXml.normalizeDocument();
            comparisonXml.normalizeDocument();

            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreComments(true);
            XMLUnit.setIgnoreWhitespace(true);

            return XMLUnit.compareXML(baselineXml, comparisonXml).similar();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new TransformationUtilityException("An exception has happened when comparing the two XML files", e);
        }
    }

}
