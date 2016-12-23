package com.paypal.butterfly.utilities.operations.properties;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.operations.EolBufferedReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.paypal.butterfly.utilities.operations.EolHelper.removeEol;

/**
 * Operation to remove a property from a properties file.
 *
 * @author facarvalho
 */
public class RemoveProperty extends TransformationOperation<RemoveProperty> {

    private static final String DESCRIPTION = "Remove property '%s' from file '%s'";

    private String propertyName;

    public RemoveProperty() {
    }

    /**
     * Operation to remove a property from a properties file.
     *
     * @param propertyName name to the property to be removed
     */
    public RemoveProperty(String propertyName) {
        setPropertyName(propertyName);
    }

    public RemoveProperty setPropertyName(String propertyName) {
        checkForBlankString("Property Name", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }


    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        String details;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        TOExecutionResult result = null;
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        File tempFile = new File(fileToBeChanged.getAbsolutePath() + "_temp_" + System.currentTimeMillis());
        try {
            if (!fileToBeChanged.exists()) {
                // TODO Should this be done as pre-validation?
                details = String.format("Operation '%s' hasn't transformed the application because file '%s', where the property removal should happen, does not exist", getName(), getRelativePath(transformedAppFolder, fileToBeChanged));
                return TOExecutionResult.noOp(this, details);
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeChanged), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
            String currentLine;
            boolean foundFirstMatch = false;
            String regex = "("+propertyName+".*)";
            final Pattern pattern = Pattern.compile(regex);
            EolBufferedReader eolReader = new EolBufferedReader(reader);
            while((currentLine = eolReader.readLineKeepStartEol()) != null) {
                if(!foundFirstMatch && pattern.matcher(removeEol(currentLine)).matches()) {
                    foundFirstMatch = true;
                    continue;
                }
                writer.write(currentLine);
            }
            //If it founds first match means, then it would be removed
            if (foundFirstMatch) {
                details = String.format("Property '%s' has been removed from '%s'", propertyName, getRelativePath());
                result = TOExecutionResult.success(this, details);
            } else {
                details = String.format("Property '%s' has NOT been removed from '%s' because it is not present on it", propertyName, getRelativePath());
                result = TOExecutionResult.warning(this, details);
            }
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

        boolean deleted = fileToBeChanged.delete();
        if(deleted) {
            if (!tempFile.renameTo(fileToBeChanged)) {
                details = String.format("Error when renaming temporary file %s to %s", getRelativePath(transformedAppFolder, tempFile), getRelativePath(transformedAppFolder, fileToBeChanged));
                TransformationOperationException e = new TransformationOperationException(details);
                result = TOExecutionResult.error(this, e);
            }
        } else {
            details = String.format("Error when deleting %s", getRelativePath(transformedAppFolder, fileToBeChanged));
            TransformationOperationException e = new TransformationOperationException(details);
            result = TOExecutionResult.error(this, e);
        }
        return result;
    }

    @Override
    public RemoveProperty clone() throws CloneNotSupportedException {
        RemoveProperty clonedRemoveProperty = (RemoveProperty) super.clone();
        return clonedRemoveProperty;
    }



}
