package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.DoubleCondition;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Compares two files and returns true only if their contents are identical.
 * <br>
 * See {@link com.paypal.butterfly.extensions.api.DoubleCondition} to find out how to set the baseline and the comparison files
 *
 * @author facarvalho
 */
public class CompareFiles extends DoubleCondition<CompareFiles> {

    private static final String DESCRIPTION = "Compare file %s to another one, return true only if their contents are equal";

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

}
