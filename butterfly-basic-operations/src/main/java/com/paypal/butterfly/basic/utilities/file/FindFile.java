package com.paypal.butterfly.basic.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * @see {@link FindFiles} for a better refined search
 * and to find multiple files
 *
 * @author facarvalho
 */
public class FindFile extends TransformationUtility<FindFile, File> {

    private static final Logger logger = LoggerFactory.getLogger(FindFile.class);

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
    protected File execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File searchRootFolder = getAbsoluteFile(transformedAppFolder, transformationContext);
        FindFiles findFiles = new FindFiles(fileName, true);
        List<File> files = findFiles.execution(searchRootFolder, transformationContext);

        if(files == null || files.size() == 0) {
            logger.warn("No file named '{}' has been found by {}", fileName, getName());
            return null;
        }
        if(files.size() > 1) {
            logger.debug("More than one file named {} has been found by {}, the first one will be returned", fileName, getName());
        }

        return files.get(0);
    }

}
