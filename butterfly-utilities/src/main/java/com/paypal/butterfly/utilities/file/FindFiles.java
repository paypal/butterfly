package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility to find files based on a regular expression
 * against the file name. The search might be recursive (including sub-folders) or not
 * </br>
 * The root directory from where the search should take place
 * can be defined by {@link #relative(String)},
 * {@link #absolute(String)} or {@link #absolute(String, String)}.
 * If not set explicitly, then the search will happen from the root
 * of the transformed application, which is equivalent to setting
 * {@link #relative(String) to {@code "."}
 * </br>
 * If no files have been found, an empty list is returned and a
 * warning is stated in the result
 *
 * @author facarvalho
 */
public class FindFiles extends TransformationUtility<FindFiles> {

    private static final String DESCRIPTION = "Find files whose name accepts regular expression %s and are under folder %s%s";

    private String regex;
    private boolean recursive;

    public FindFiles() {
    }

    /**
     * Utility to find files based on a regular expression
     * against the file name. The search might be recursive (including sub-folders) or not
     * </br>
     * The root directory from where the search should take place
     * can be defined by {@link #relative(String)},
     * {@link #absolute(String)} or {@link #absolute(String, String)}.
     * If not set explicitly, then the search will happen from the root
     * of the transformed application, which is equivalent to setting
     * {@link #relative(String) to {@code "."}
     *
     * @param regex regular expression to be applied against file name during search
     * @param recursive if true, sub-folders will also be searched
     */
    public FindFiles(String regex, boolean recursive) {
        setRegex(regex);
        setRecursive(recursive);
    }

    public FindFiles setRegex(String regex) {
        checkForBlankString("Regex", regex);
        this.regex = regex;
        return this;
    }

    public FindFiles setRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, regex, getRelativePath(), (recursive ? " and sub-folders" : " only (not including sub-folders)"));
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File searchRootFolder = getAbsoluteFile(transformedAppFolder, transformationContext);
        IOFileFilter fileFilter = new AbstractFileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().matches(regex);
            }
        };
        Collection<File> files = FileUtils.listFiles(searchRootFolder, fileFilter, (recursive ? TrueFileFilter.INSTANCE : null));
        TUExecutionResult result = null;

        if(files.size() == 0) {
            result = TUExecutionResult.warning(this, new ArrayList<File>(files), "No files have been found");
        } else {
            result = TUExecutionResult.value(this, new ArrayList<File>(files));
        }

        return result;
    }

}
