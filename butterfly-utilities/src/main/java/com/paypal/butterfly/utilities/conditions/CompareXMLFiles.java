package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.DoubleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Compares two XML files and returns true if their contents are equal,
 * or if both files don't exist. Returns false otherwise.
 * Attribute orders, comments and white spaces are ignored during the comparison.
 * It results in error if any of the two files is not a well formed XML file.
 * <br>
 * See {@link DoubleCondition}
 * to find out how to set the baseline and the comparison files
 *
 * @author facarvalho
 */
public class CompareXMLFiles extends DoubleCondition<CompareXMLFiles> {

    private static final String DESCRIPTION = "Compare XML file %s to another one, return true only if their contents are equal";

    /**
     * Compares two XML files and returns true if their contents are equal,
     * or if both files don't exist. Returns false otherwise.
     * Attribute orders, comments and white spaces are ignored during the comparison.
     * It results in error if any of the two files is not a well formed XML file.
     * <br>
     * See {@link DoubleCondition}
     * to find out how to set the baseline and the comparison files
     */
    public CompareXMLFiles() {
    }

    /**
     * Compares two XML files and returns true if their contents are equal,
     * or if both files don't exist. Returns false otherwise.
     * Attribute orders, comments and white spaces are ignored during the comparison.
     * It results in error if any of the two files is not a well formed XML file.
     * <br>
     * See {@link DoubleCondition}
     * to find out how to set the baseline and the comparison files
     *
     * @param attribute the name of the transformation context attribute
     *                  that refers to the file to be compared against the baseline file
     */
    public CompareXMLFiles(String attribute) {
        setAttribute(attribute);
    }

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
            throw new TransformationUtilityException("An exception happened when comparing the two XML files", e);
        }
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        return super.execution(transformedAppFolder, transformationContext);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

}
