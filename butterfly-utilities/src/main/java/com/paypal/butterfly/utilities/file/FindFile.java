package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Finds a file based on its name. The search is
 * always recursive (includes sub-folders). If no file is
 * found, null is returned, unless {@link #failIfNotFound(boolean)} is set to true.
 * If multiple files are found, an error is returned.
 * <br>
 * The root directory from where the search should take place
 * can be defined by {@link #relative(String)},
 * {@link #absolute(String)} or {@link #absolute(String, String)}.
 * If not set explicitly, then the search will happen from the root
 * of the transformed application, which is equivalent to setting
 * {@link #relative(String)} to {@code "."}
 * <br>
 * If no file is found, a {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL}
 * is returned, unless {@link #failIfNotFound(boolean)} is set to true
 * <br>
 * See {@link FindFiles} for a better refined search and to find multiple files.
 *
 * @author facarvalho
 */
public class FindFile extends TransformationUtility<FindFile> {

    private static final String DESCRIPTION = "Find file named %s under %s";

    // Name of the file to be found
    private String fileName;

    private boolean failIfNotFound = false;

    public FindFile() {
    }

    /**
     * Utility to find a file based on its name
     *
     * @param fileName name of the file to be found
     */
    public FindFile(String fileName) {
        setFileName(fileName);
    }

    public FindFile setFileName(String fileName) {
        checkForBlankString("File Name", fileName);
        this.fileName = fileName;
        return this;
    }

    public FindFile failIfNotFound(boolean failIfNotFound) {
        this.failIfNotFound = failIfNotFound;
        return this;
    }

    public boolean isFailIfNotFound() {
        return failIfNotFound;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, fileName, ("".equals(getRelativePath()) ? "root of application" : getRelativePath()));
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        if (StringUtils.isBlank(fileName)) {
            // TODO This should be done as pre-validation and throw a specific pre-validation exception
            TransformationUtilityException ex = new TransformationUtilityException("File name has not been set");
            return TUExecutionResult.error(this, ex);
        }

        File searchRootFolder = getAbsoluteFile(transformedAppFolder, transformationContext);

        if (!searchRootFolder.exists()) {
            String details = String.format("No file named '%s' has been found by %s", fileName, getName());
            if (failIfNotFound) {
                TransformationUtilityException e = new TransformationUtilityException(details);
                return TUExecutionResult.error(this, e);
            } else {
                return TUExecutionResult.nullResult(this, details);
            }
        }

        FindFiles findFiles = new FindFiles(fileName, true);

        TUExecutionResult result = null;

        List<File> files = (List<File>) findFiles.execution(searchRootFolder, transformationContext).getValue();

        if(files == null || files.size() == 0) {
            String details = String.format("No file named '%s' has been found", fileName);
            if (failIfNotFound) {
                TransformationUtilityException e = new TransformationUtilityException(details);
                result = TUExecutionResult.error(this, e);
            } else {
                result = TUExecutionResult.nullResult(this, details);
            }
        } else if(files.size() > 1) {
            String exceptionMessage = String.format("More than one file named %s have been found", fileName);
            TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
            result = TUExecutionResult.error(this, e);
        } else if(files.size() == 1) {
            result = TUExecutionResult.value(this, files.get(0));
        }

        return result;
    }

}
