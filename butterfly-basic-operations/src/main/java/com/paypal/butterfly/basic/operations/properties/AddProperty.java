package com.paypal.butterfly.basic.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Operation to add a new property to a properties file.
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
            if(fileInputStream != null) {
                fileInputStream.close();
            }
            //Add Property
            if(!properties.containsKey(propertyName)) {
                result = addProperty(transformedAppFolder, transformationContext);
            } else {
                //Set Property
                result = setProperty(transformedAppFolder, transformationContext, fileInputStream);
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

    @Override
    public AddProperty clone() throws CloneNotSupportedException {
        AddProperty clonedAddProperty = (AddProperty) super.clone();
        return clonedAddProperty;
    }

    /**
     * Replace the text based on regex.
     * @param reader
     * @param writer
     * @param regex
     * @param replacement
     * @return String
     * @throws IOException
     */
    private String replace(BufferedReader reader, BufferedWriter writer, String regex, String replacement) throws IOException {
        String currentLine;
        int n = 0;
        boolean foundFirstMatch = false;
        final Pattern pattern = Pattern.compile(regex + "(.*)");
        boolean firstLine = true;
        while((currentLine = reader.readLine()) != null) {
            if(!foundFirstMatch && pattern.matcher(currentLine).matches()) {
                foundFirstMatch = true;
                n++;
                //Replace the Property Key and Value (entire line)
                currentLine = currentLine.replaceAll(".+", replacement);
            }
            if(!firstLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(currentLine);
            firstLine = false;
        }

        return String.format("File %s has had %d line(s) where property setting was applied based on regular expression '%s'", getRelativePath(), n, regex);
    }


    /**
     * To add a new property to the property file.
     * @param transformedAppFolder
     * @param transformationContext
     * @return TOExecutionResult
     */
    private TOExecutionResult addProperty(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        String details = null;
        TOExecutionResult result = null;
        try {
            String[] propArray = {propertyName, propertyValue};
            String propertyKeyValue = "%s = %s";
            String propertyToBeAdded = String.format(propertyKeyValue, propArray);
            FileUtils.fileAppend(fileToBeChanged.getAbsolutePath(), System.lineSeparator());
            FileUtils.fileAppend(fileToBeChanged.getAbsolutePath(), propertyToBeAdded);
            details = String.format("Property '%s' has been added and set to '%s' at '%s'", propertyName, propertyValue, getRelativePath());
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        }
        return result;
    }

    /**
     *
     * @param transformedAppFolder
     * @param transformationContext
     * @param fileInputStream
     * @return TOExecutionResult
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    private TOExecutionResult setProperty(File transformedAppFolder, TransformationContext transformationContext, FileInputStream fileInputStream) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        TOExecutionResult result = null;
        String details = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = null;
        try {
            tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
            String[] propArray = {propertyName, propertyValue};
            String propertyKeyValue = "%s = %s";
            String propertyToBeAdded = String.format(propertyKeyValue, propArray);
            details = replace(reader, writer, "("+propertyName+")", propertyToBeAdded);
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
        boolean bDeleted = fileToBeChanged.delete();
        if(bDeleted) {
            if (!tempFile.renameTo(fileToBeChanged)) {
                details = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
                TransformationOperationException e = new TransformationOperationException(details);
                result = TOExecutionResult.error(this, e);
            }
        }else {
            details = String.format("Error when deleting %s", getRelativePath(transformedAppFolder, fileToBeChanged));
            TransformationOperationException e = new TransformationOperationException(details);
            result = TOExecutionResult.error(this, e);
        }

        return result;
    }


}