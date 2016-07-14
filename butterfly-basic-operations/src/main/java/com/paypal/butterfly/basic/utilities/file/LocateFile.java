package com.paypal.butterfly.basic.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;

/**
 * Utility to locate a file based on the relative or absolute
 * locations specified. It does not find files, it just results
 * to a {@link File} object based on the input information.
 *
 * @author facarvalho
 */
public class LocateFile extends TransformationUtility<LocateFile, File> {

    private static final String DESCRIPTION = "Locate file under %s";

    public LocateFile() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected File execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        return getAbsoluteFile(transformedAppFolder, transformationContext);
    }

}
