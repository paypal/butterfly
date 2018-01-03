package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Locates a file based on the relative or absolute
 * location specified. It does not find files, it just results
 * to a {@link File} object based on the input information.
 * This utility also allows to locate a file going up in parent
 * levels from the specified file. If the specified file does
 * not exist, or if the coordinates don't make sense, an error
 * is returned.
 * <br>
 * Note: the term "file" here might refer to a folder as well
 *
 * @author facarvalho
 */
public class LocateFile extends TransformationUtility<LocateFile> {

    private static final String DESCRIPTION_PARENT_ZERO = "Locate file %s";
    private static final String DESCRIPTION_PARENT = "Locate file %d levels above %s";

    private int parentLevel = 0;

    /**
     * Locates a file based on the relative or absolute
     * location specified. It does not find files, it just results
     * to a {@link File} object based on the input information.
     * This utility also allows to locate a file going up in parent
     * levels from the specified file. If the specified file does
     * not exist, or if the coordinates don't make sense, an error
     * is returned
     * <br>
     * Note: the term "file" here might refer to a folder as well
     */
    public LocateFile() {
    }

    /**
     * Locates a file based on the relative or absolute
     * location specified. It does not find files, it just results
     * to a {@link File} object based on the input information.
     * This utility also allows to locate a file going up in parent
     * levels from the specified file. If the specified file does
     * not exist, or if the coordinates don't make sense, an error
     * is returned
     * <br>
     * Note: the term "file" here might refer to a folder as well
     *
     * @param parentLevel how many parent levels to be located
     */
    public LocateFile(int parentLevel) {
        setParentLevel(parentLevel);
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
        String location = getRelativePath();
        if (StringUtils.isBlank(location)) {
            location = "root folder";
        }

        // FIXME
        // An usage like the one below results in a wrong description.
        // Instead of resulting in something like "Locate file '/foo/bar/init'" it results in "Locate file root folder"
        //
        // add(new LocateFile().absolute(APP_PACKAGE_LOCATION, "init");

        if (parentLevel == 0) {
            return String.format(DESCRIPTION_PARENT_ZERO, location);
        } else {
            return String.format(DESCRIPTION_PARENT, parentLevel, location);
        }
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
                TransformationUtilityException e = new TransformationUtilityException(message);
                result = TUExecutionResult.error(this, e);
            } else if (!locatedFile.exists()) {
                String message = String.format("File to be located does not exist");
                TransformationUtilityException e = new TransformationUtilityException(message);
                result = TUExecutionResult.error(this, e);
            } else {
                result = TUExecutionResult.value(this, locatedFile);
            }
            // FIXME a better exception is necessary here for cases when the absolute path transformation context attribute value is null
        } catch(TransformationUtilityException exception) {
            String details = String.format("No file has been located by %s because its baseline relative or absolute location could not be resolved", getName());
            result = TUExecutionResult.error(this, null, exception, details);
        }

        return result;

    }

}
