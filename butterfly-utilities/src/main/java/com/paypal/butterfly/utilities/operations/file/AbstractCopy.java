package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * Abstract copy operation to be specialized for more specific purposes, such as single
 * file copy, or directory copy.
 *
 * @see CopyFile
 * @see CopyDirectory
 *
 * @author facarvalho
 */
abstract class AbstractCopy<TO extends AbstractCopy> extends TransformationOperation<TO> {

    protected String description;

    // Relative location where to copy the file to
    protected String toRelative = null;

    // Name of the transformation context attribute that holds
    // the absolute location where to copy the file to
    // Only one should be set between this and {@code toRelative}
    protected String toAbsoluteAttribute = null;

    // An additional relative path to be added to the absolute file
    // coming from the transformation context
    protected String additionalRelativePath = null;

    /**
     * Abstract copy operation to be specialized for more specific purposes, such as single
     * file copy, or directory copy.
     *
     * @see CopyFile
     * @see CopyDirectory
     */
    protected AbstractCopy(String description) {
        setDescription(description);
    }

    public void setDescription(String description) {
        checkForBlankString("Description", description);
        this.description = description;
    }

    /**
     * Set relative location where to copy the file to.
     * <br>
     * If the relative location is NOT known during transformation definition time,
     * then don't set it (leaving as {@code null}) and use {@link #setToAbsolute(String)}
     * based on a transformation context attribute set by a
     * {@link com.paypal.butterfly.utilities.file.LocateFile}
     * transformation utility.
     * <br>
     * By setting this relative location, the absolute location attribute name is automatically set to {@code null}
     *
     * @param toRelative relative location where to copy the file to
     * @return this transformation operation instance
     */
    public TO setToRelative(String toRelative) {
        checkForBlankString("Relative Location", toRelative);
        this.toRelative = toRelative;
        this.toAbsoluteAttribute = null;
        return (TO) this;
    }

    /**
     * The name of the transformation context attribute that holds
     * the absolute location where to copy the file to.
     * <br>
     * If the relative location is known during transformation definition time,
     * then don't use this setter, use {@link #setToRelative(String)} instead.
     * <br>
     * By setting this attribute name, the relative location is automatically set to {@code null}
     *
     * @param attributeName name of the transformation context attribute that holds
     *                      the absolute location where to copy the file to
     * @return this transformation operation instance
     */
    public TO setToAbsolute(String attributeName) {
        checkForBlankString("Absolute Location", attributeName);
        this.toAbsoluteAttribute = attributeName;
        this.toRelative = null;
        return (TO) this;
    }

    /**
     * The name of the transformation context attribute that holds
     * the absolute location where to copy the file to.
     * <br>
     * If the relative location is known during transformation definition time,
     * then don't use this setter, use {@link #setToRelative(String)} instead.
     * <br>
     * By setting this attribute name, the relative location is automatically set to {@code null}
     *
     * @param attributeName name of the transformation context attribute that holds
     *                      the absolute location where to copy the file to
     * @param additionalRelativePath an additional relative path to be added to the absolute
     *                               file coming from the transformation context. The path
     *                               separator will be normalized, similar to what happens
     *                               in {@link #relative(String)}
     * @return this transformation operation instance
     */
    public TO setToAbsolute(String attributeName,  String additionalRelativePath) {
        checkForBlankString("attributeName", attributeName);
        checkForBlankString("additionalRelativePath", additionalRelativePath);
        this.toAbsoluteAttribute = attributeName;
        this.additionalRelativePath = normalizeRelativePathSeparator(additionalRelativePath);
        this.toRelative = null;
        return (TO) this;
    }

    public String getToRelative() {
        return toRelative;
    }

    public String getToAbsoluteAttribute() {
        return toAbsoluteAttribute;
    }

    @Override
    public String getDescription() {
        return String.format(description, getRelativePath(), (toRelative != null ? toRelative : "the location defined by transformation context attribute " + toAbsoluteAttribute));
    }

    @Override
    public AbstractCopy<TO> clone() throws CloneNotSupportedException {
        AbstractCopy<TO> clone = (AbstractCopy<TO>) super.clone();
        return clone;
    }

}