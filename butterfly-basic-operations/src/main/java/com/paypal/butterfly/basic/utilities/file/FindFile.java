package com.paypal.butterfly.basic.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;

/**
 * Utility to find a file based on its name. The search is
 * always recursive (includes sub-folders). If multiple files
 * are found, the first one only is returned. If no file is
 * found, null is returned.
 *
 * @see {@link FindFiles} for a better refined search
 *
 * @author facarvalho
 */
public class FindFile extends TransformationUtility<FindFile, File> {

    private static final Logger logger = LoggerFactory.getLogger(FindFile.class);

    private static final String DESCRIPTION = "Find file named %s under %s";

    private String fileName;

    public FindFile() {
    }

    /**
     * Utility to find a file based on its name
     *
     * @param fileName name of the file to be found
     */
    public FindFile(String fileName) {
        this.fileName = fileName;
    }

    public FindFile setFileName(String fileName) {
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
        FindFiles findFiles = new FindFiles(fileName, true);
        List<File> files = findFiles.execution(transformedAppFolder, transformationContext);

        if(files == null || files.size() == 0) {
            logger.debug("No file named {} has been found by {}", fileName, getName());
            return null;
        }
        if(files.size() > 1) {
            logger.debug("More than one file named {} has been found by {}, the first one will be returned", fileName, getName());
        }

        return files.get(0);
    }

}
