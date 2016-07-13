package com.paypal.butterfly.basic.utilities.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility to find files based on a regular expression
 * against the file name. The search might be recursive (including sub-folders)or not
 *
 * @author facarvalho
 */
public class FindFiles extends TransformationUtility<FindFiles, List<File>> {

    private static final String DESCRIPTION = "Find files whose name accepts regular expression %s and are under folder %s%s";

    private String regex;
    private boolean recursive;

    public FindFiles() {
    }

    /**
     * Utility to find files
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private FindFiles(String relativePath) {
        super(relativePath);
    }

    /**
     * Utility to find a files
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     * @param regex regular expression to be applied against file name during search
     * @param recursive if true, sub-folders will also be searched
     */
    public FindFiles(String relativePath, String regex, boolean recursive) {
        this(relativePath);
        this.regex = regex;
        this.recursive = recursive;
    }

    public FindFiles setRegex(String regex) {
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
    protected List<File> execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File rootSearchFolder = getAbsoluteFile(transformedAppFolder, transformationContext);

        IOFileFilter fileFilter = new AbstractFileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().matches(regex));
            }
        };
        Collection<File> files = FileUtils.listFiles(rootSearchFolder, fileFilter, (recursive ? TrueFileFilter.INSTANCE : null));

        return new ArrayList<File>(files);
    }

}
