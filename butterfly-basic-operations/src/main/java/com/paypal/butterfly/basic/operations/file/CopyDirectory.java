package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Operation to copy a directory and its content from one location to another.
 * The files to be copied include sub-folders and their files, coming rom relative
 * or absolute location. The path to the files to be copied are preserved, and those
 * folders are also copied to the destination location. If the destination directory
 * does not exist, it is created. But, if it does, then the content to be copied is
 * merged with the destination content, with the source taking precedence.
 * </br>
 * <strong>Note:<strong/> if all you want is to copy a set of specific files from one
 * location to another, then use a multiple transformation operation
 * (see {@code TransformationTemplate.addMultiple()}) with {@link CopyFile}
 *
 * @see {@link CopyFile}
 *
 * @author facarvalho
 */
public class CopyDirectory extends TransformationOperation<CopyDirectory> {

    private static final String DESCRIPTION = "Copy files set by transformation context attribute %s to %s";

    // Relative location where to copy the file to
    private String toRelative = null;

    // Name of the transformation context attribute that holds
    // the absolute location where to copy the file to
    // Only one should be set between this and {@code toRelative}
    private String toAbsoluteAttribute = null;

    /**
     * Operation to copy a directory and its content from one location to another.
     * The files to be copied include sub-folders and their files, coming rom relative
     * or absolute location. The path to the files to be copied are preserved, and those
     * folders are also copied to the destination location. If the destination directory
     * does not exist, it is created. But, if it does, then the content to be copied is
     * merged with the destination content, with the source taking precedence.
     *
     * @author facarvalho
     */
    public CopyDirectory() {
    }

    /**
     * Set relative location where to copy the file to.
     * </br>
     * If the relative location is NOT known during transformation definition time,
     * then don't set it (leaving as {@code null) and use {@link #setToAbsolute(String)}
     * based on a transformation context attribute set by a
     * {@link com.paypal.butterfly.basic.utilities.file.LocateFile}
     * transformation utility.
     * </br>
     * By setting this relative location, the absolute location attribute name is automatically set to {@code null}
     *
     * @param toRelative relative location where to copy the file to
     */
    public CopyDirectory setToRelative(String toRelative) {
        this.toRelative = toRelative;
        this.toAbsoluteAttribute = null;
        return this;
    }

    /**
     * The name of the transformation context attribute that holds
     * the absolute location where to copy the file to.
     * </br>
     * If the relative location is known during transformation definition time,
     * then don't use this setter, use {@link #setToRelative(String)} instead.
     * </br>
     * By setting this attribute name, the relative location is automatically set to {@code null}
     *
     * @param attributeName name of the transformation context attribute that holds
     *                      the absolute location where to copy the file to
     * @return this transformation operation instance
     */
    public CopyDirectory setToAbsolute(String attributeName) {
        this.toAbsoluteAttribute = attributeName;
        this.toRelative = null;
        return this;
    }

    public String getToRelative() {
        return toRelative;
    }

    public String getToAbsoluteAttribute() {
        return toAbsoluteAttribute;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), (toRelative != null ? toRelative : "the location defined by transformation context attribute " + toAbsoluteAttribute));
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO Validation must be done here!!! In case none has been set!
        File filesFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        FileUtils.copyDirectory(filesFrom, fileTo);

        return String.format("Files from '%s' copied to '%s'", getRelativePath(transformedAppFolder, filesFrom), getRelativePath(transformedAppFolder, fileTo));
    }

    private File getFileTo(File transformedAppFolder, TransformationContext transformationContext) {
        File fileTo;
        if(toRelative != null) {
            fileTo = new File(transformedAppFolder, toRelative);
        } else {
            fileTo = (File) transformationContext.get(toAbsoluteAttribute);
        }

        return fileTo;
    }

    @Override
    public CopyDirectory clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}