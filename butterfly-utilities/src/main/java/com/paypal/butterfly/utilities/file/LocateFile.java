package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;

/**
 * Utility to locate a file based on the relative or absolute
 * location specified. It does not find files, it just results
 * to a {@link File} object based on the input information.
 * This utility also allows to locate a file going up in parent
 * levels from the specified file.
 *
 * @author facarvalho
 */
public class LocateFile extends TransformationUtility<LocateFile> {

    private static final String DESCRIPTION = "Locate file under %s";

    private int parentLevel = 0;

    public LocateFile() {
    }

    /**
     * Set how many parent levels up the location process should
     * go through. If not set, the actual specified file will be
     * the located one.
     *
     * @param parentLevel how many parent levels to be located
     *
     * @return this instance
     */
    public LocateFile setParentLevel(int parentLevel) {
        if(parentLevel < 0) {
            throw new IllegalArgumentException("Invalid parent level");
        }

        this.parentLevel = parentLevel;
        return this;
    }

    public int getParentLevel() {
        return parentLevel;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File locatedFile;
        TUExecutionResult result = null;

        try {
            locatedFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            for(int i = parentLevel; i > 0; i--) {
                locatedFile = locatedFile.getParentFile();
                if (locatedFile == null) {
                    break;
                }
            }
            if (locatedFile == null) {
                String message = String.format("File to be located reached limit of files hierarchy, parent level %d is too deep", parentLevel);
                TransformationOperationException e = new TransformationOperationException(message);
                result = TUExecutionResult.error(this, e);
            } else {
                result = TUExecutionResult.value(this, locatedFile);
            }
            // FIXME a better exception is necessary here for cases when the absolute path transformation context attribute value is null
        } catch(TransformationUtilityException exception) {
            String details = String.format("No file has been located by %s because its baseline relative or absolute location could not be resolved", getName());
            result = TUExecutionResult.error(this, exception, details);
        }

        return result;

    }

}
