package com.paypal.butterfly.utilities.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;
import com.paypal.butterfly.utilities.operations.EolHelper;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolHelper.removeEol;

/**
 * Adds a new property to a properties file.
 * If the property already exists, its value is overwritten.
 *
 * @author facarvalho
 */
public class AddProperty extends TransformationOperation<AddProperty> {

    private static final String DESCRIPTION = "Add new property ('%s' = '%s') to file '%s'";

    private String propertyName;
    private String propertyValue;

    public AddProperty() {
    }

    /**
     * Operation to add a new property to a properties file.
     * If the property already exists, its value is overwritten.
     *
     * @param propertyName name to the property to be added
     * @param propertyValue value to be set to the new property
     */
    public AddProperty(String propertyName, String propertyValue) {
        setPropertyName(propertyName);
        setPropertyValue(propertyValue);
    }

    public AddProperty setPropertyName(String propertyName) {
        checkForBlankString("Property Name", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    public AddProperty setPropertyValue(String propertyValue) {
        checkForBlankString("Property Value", propertyValue);
        this.propertyValue = propertyValue;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, propertyValue, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        FileInputStream fileInputStream = null;
        TOExecutionResult result = null;
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        try {
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(fileToBeChanged);
            properties.load(fileInputStream);
            fileInputStream.close();

            //Add Property
            if(!properties.containsKey(propertyName)) {
                result = addProperty(transformedAppFolder, transformationContext);
            } else {
                //Set Property
                result = setProperty(transformedAppFolder, transformationContext);
            }
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e) {
                result.addWarning(e);
            }
        }
        return result;
    }

    /*
     * Replace the text based on regex.
     */
    private String replace(BufferedReader reader, BufferedWriter writer, String regex, String replacement) throws IOException {
        String currentLine;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex + "(.*)");
        EolBufferedReader eolReader = new EolBufferedReader(reader);
        while((currentLine = eolReader.readLineKeepStartEol()) != null) {
            if(!foundFirstMatch && pattern.matcher(removeEol(currentLine)).matches()) {
                foundFirstMatch = true;
                //Replace the Property Key and Value (entire line)
                currentLine = currentLine.replaceAll(".+", replacement);
            }
            writer.write(currentLine);
        }

        return String.format("Property '%s' value replaced with %s' at '%s'", propertyName, propertyValue, getRelativePath());
    }

    /*
     * To add a new property to the property file.
     */
    private TOExecutionResult addProperty(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        TOExecutionResult result;
        try {
            String[] propArray = {propertyName, propertyValue};
            String propertyKeyValue = "%s = %s";
            String propertyToBeAdded = String.format(propertyKeyValue, propArray);
            FileUtils.fileAppend(fileToBeChanged.getAbsolutePath(), EolHelper.findEolDefaultToOs(fileToBeChanged));
            FileUtils.fileAppend(fileToBeChanged.getAbsolutePath(), propertyToBeAdded);
            String details = String.format("Property '%s' has been added and set to '%s' at '%s'", propertyName, propertyValue, getRelativePath());
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        }
        return result;
    }

    /**
     * To Set (Replace) the property value.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    private TOExecutionResult setProperty(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        TOExecutionResult result = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            File readFile = getOrCreateReadFile(transformedAppFolder, transformationContext);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToBeChanged), StandardCharsets.UTF_8));
            String[] propArray = {propertyName, propertyValue};
            String propertyKeyValue = "%s = %s";
            String propertyToBeAdded = String.format(propertyKeyValue, propArray);
            String details = replace(reader, writer, "(" + propertyName + ")", propertyToBeAdded);
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            try {
                if (writer != null) try {
                    writer.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            } finally {
                if(reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }


}