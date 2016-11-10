package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * This utility condition compares two files and returns true only
 * if their contents are identical.
 * </br>
 * The first file is the one specified by {@link #relative(String)}
 * or {@link #absolute(String)}, while the second one is specified
 * as a String containing the name of the transformation context
 * attribute that holds the file.
 *
 * @author facarvalho
 */
public class CompareFiles extends AbstractCompareFiles<CompareFiles> {

    @Override
    protected boolean compare(File baselineFile, File comparisonFile) {
        try {
            return FileUtils.contentEquals(baselineFile, comparisonFile);
        } catch (IOException e) {
            throw new TransformationUtilityException("An exception has happened when comparing files", e);
        }
    }

}
