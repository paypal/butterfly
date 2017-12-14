package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.DoubleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Compares two files and returns true if the content of the files are equal,
 * or if they both don't exist. Returns false otherwise.
 * <br>
 * See {@link com.paypal.butterfly.extensions.api.DoubleCondition} to find out how to set the baseline and the comparison files
 *
 * @author facarvalho
 */
public class CompareFiles extends DoubleCondition<CompareFiles> {

    private static final String DESCRIPTION = "Compare file %s to another one, return true only if their contents are equal";

    /**
     * Compares two files and returns true if the content of the files are equal,
     * or if they both don't exist. Returns false otherwise.
     * <br>
     * See {@link com.paypal.butterfly.extensions.api.DoubleCondition} to find out how to set the baseline and the comparison files
     */
    public CompareFiles(){
    }

    /**
     * Compares two files and returns true if the content of the files are equal,
     * or if they both don't exist. Returns false otherwise.
     * <br>
     * See {@link com.paypal.butterfly.extensions.api.DoubleCondition} to find out how to set the baseline and the comparison files
     *
     * @param attribute the name of the transformation context attribute
     *                  that refers to the file to be compared against the baseline file
     */
    public CompareFiles(String attribute){
        super(attribute);
    }

    @Override
    protected boolean compare(File baselineFile, File comparisonFile) {
        try {
            return FileUtils.contentEquals(baselineFile, comparisonFile);
        } catch (IOException e) {
            throw new TransformationUtilityException("An exception has happened when comparing files", e);
        }
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        return super.execution(transformedAppFolder, transformationContext);
    }

}
