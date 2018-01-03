package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Abstract "to" operation whenever a destination file path needs to be set.
 * To be specialized for more specific purposes, such as single
 * file copy, file move, directory copy or directory move.
 *
 * @see CopyFile
 * @see CopyDirectory
 * @see MoveFile
 * @see MoveDirectory
 *
 * @author facarvalho
 */
abstract class AbstractToOperation<A extends AbstractToOperation> extends TransformationOperation<A> {

    protected String description;

    // Relative destination location
    protected String toRelative = null;

    // Name of the transformation context attribute that holds
    // the absolute destination location.
    // Only one should be set between this and {@code toRelative}
    protected String toAbsoluteAttribute = null;

    // An additional relative path to be added to the absolute file
    // coming from the transformation context
    protected String additionalRelativePath = null;

    /**
     * Abstract "to" operation whenever a destination file path needs to be set.
     * To be specialized for more specific purposes, such as single
     * file copy, file move, directory copy or directory move.
     *
     * @see CopyFile
     * @see CopyDirectory
     * @see MoveFile
     * @see MoveDirectory
     */
    protected AbstractToOperation(String description) {
        setDescription(description);
    }

    public A setDescription(String description) {
        checkForBlankString("Description", description);
        this.description = description;
        return (A) this;
    }

    /**
     * Set relative destination location.
     * <br>
     * If the relative destination location is NOT known during transformation definition time,
     * then don't set it (leaving as {@code null}) and use {@link #setToAbsolute(String)}
     * based on a transformation context attribute set by a
     * {@link com.paypal.butterfly.utilities.file.LocateFile}
     * transformation utility.
     * <br>
     * By setting this relative location, the absolute location attribute name is automatically set to {@code null}
     *
     * @param toRelative relative destination location
     * @return this transformation operation instance
     */
    public A setToRelative(String toRelative) {
        checkForBlankString("Relative Location", toRelative);
        this.toRelative = toRelative;
        this.toAbsoluteAttribute = null;
        return (A) this;
    }

    /**
     * The name of the transformation context attribute that holds
     * the absolute destination location.
     * <br>
     * If the relative destination location is known during transformation definition time,
     * then don't use this setter, use {@link #setToRelative(String)} instead.
     * <br>
     * By setting this attribute name, the relative destination location is automatically set to {@code null}
     *
     * @param attributeName name of the transformation context attribute that holds
     *                      the absolute destination location
     * @return this transformation operation instance
     */
    public A setToAbsolute(String attributeName) {
        checkForBlankString("Absolute Location", attributeName);
        this.toAbsoluteAttribute = attributeName;
        this.toRelative = null;
        return (A) this;
    }

    /**
     * The name of the transformation context attribute that holds
     * the absolute destination location.
     * <br>
     * If the relative destination location is known during transformation definition time,
     * then don't use this setter, use {@link #setToRelative(String)} instead.
     * <br>
     * By setting this attribute name, the relative destination location is automatically set to {@code null}
     *
     * @param attributeName name of the transformation context attribute that holds
     *                      the absolute destination location
     * @param additionalRelativePath an additional relative path to be added to the absolute
     *                               file coming from the transformation context. The path
     *                               separator will be normalized, similar to what happens
     *                               in {@link #relative(String)}
     * @return this transformation operation instance
     */
    public A setToAbsolute(String attributeName,  String additionalRelativePath) {
        checkForBlankString("attributeName", attributeName);
        checkForBlankString("additionalRelativePath", additionalRelativePath);
        this.toAbsoluteAttribute = attributeName;
        this.additionalRelativePath = normalizeRelativePathSeparator(additionalRelativePath);
        this.toRelative = null;
        return (A) this;
    }

    public String getToRelative() {
        return toRelative;
    }

    public String getToAbsoluteAttribute() {
        return toAbsoluteAttribute;
    }

    protected File getFileTo(File transformedAppFolder, TransformationContext transformationContext) {
        File fileTo;
        if(toRelative != null) {
            fileTo = new File(transformedAppFolder, toRelative);
        } else {
            if (additionalRelativePath == null) {
                fileTo = (File) transformationContext.get(toAbsoluteAttribute);
            } else {
                fileTo = new File((File) transformationContext.get(toAbsoluteAttribute), additionalRelativePath);
            }
        }

        return fileTo;
    }

    @Override
    public String getDescription() {
        return String.format(description, getRelativePath(), (toRelative != null ? toRelative : "the location defined by transformation context attribute " + toAbsoluteAttribute));
    }

}