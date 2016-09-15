package com.paypal.butterfly.basic.utilities.file;

import com.paypal.butterfly.extensions.api.TUResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;
import java.util.List;

/**
 * Utility to find a file based on its name. The search is
 * always recursive (includes sub-folders). If multiple files
 * are found, the first one only is returned. If no file is
 * found, null is returned.
 * </br>
 * The root directory from where the search should take place
 * can be defined by {@link #relative(String)},
 * {@link #absolute(String)} or {@link #absolute(String, String)}.
 * If not set explicitly, then the search will happen from the root
 * of the transformed application, which is equivalent to setting
 * {@link #relative(String) to {@code "."}
 * </br>
 * If no file is found, a {@link com.paypal.butterfly.extensions.api.TUResult.Type#NULL}
 * is returned
 *
 * @see {@link FindFiles} for a better refined search
 * and to find multiple files
 *
 * @author facarvalho
 */
public class FindFile extends TransformationUtility<FindFile> {

    private static final String DESCRIPTION = "Find file named %s under %s";

    // Name of the file to be found
    private String fileName;

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

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, fileName, ("".equals(getRelativePath()) ? "root of application" : getRelativePath()));
    }

    @Override
    protected TUResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File searchRootFolder = getAbsoluteFile(transformedAppFolder, transformationContext);
        FindFiles findFiles = new FindFiles(fileName, true);

        TUResult result = null;

        // TODO improve this after TU execution returns TUResult, instead of Result
        List<File> files = (List<File>) ((TUResult) findFiles.execution(searchRootFolder, transformationContext)).getValue();

        if(files == null || files.size() == 0) {
            String details = String.format("No file named '%s' has been found by %s", fileName, getName());
            result = TUResult.nullResult(this, details);
        } else if(files.size() > 1) {
            String details = String.format("More than one file named %s has been found by %s, the first one will be returned", fileName, getName());
            result = TUResult.warning(this, files.get(0), details);
        } else if(files.size() == 1) {
            result = TUResult.value(this, files.get(0));
        }

        return result;
    }

}
