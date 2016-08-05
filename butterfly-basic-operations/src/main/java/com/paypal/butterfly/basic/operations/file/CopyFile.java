package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Operation to copy a file
 *
 * @author facarvalho
 */
public class CopyFile extends TransformationOperation<CopyFile> {

    private static final String DESCRIPTION = "Copy file %s to %s";

    // Relative location where to copy the file to
    private String toRelative = null;

    // Name of the transformation context attribute that holds
    // the absolute location where to copy the file to
    // Only one should be set between this and {@code toRelative}
    private String toAbsoluteAttribute = null;

    /**
     * Operation to copy a file
     */
    public CopyFile() {
    }

    /**
     * Set relative location where to copy the file to.
     * </br>
     * If the relative location is NOT known during transformation definition time,
     * then don't set it (leaving as {@code null) and use {@link #setToAbsoluteAttribute(String)}
     * based on a transformation context attribute set by a
     * {@link com.paypal.butterfly.basic.utilities.file.LocateFile}
     * transformation utility.
     * </br>
     * By setting this relative location, the absolute location attribute name is automatically set to {@code null}
     *
     * @param toRelative relative location where to copy the file to
     */
    public CopyFile setToRelative(String toRelative) {
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
     * @param toAbsoluteAttribute
     * @return
     */
    public CopyFile setToAbsoluteAttribute(String toAbsoluteAttribute) {
        this.toAbsoluteAttribute = toAbsoluteAttribute;
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
        File fileFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
        File fileTo;

        if(toRelative != null) {
            fileTo = new File(transformedAppFolder, toRelative);
        } else {
            fileTo = (File) transformationContext.get(toAbsoluteAttribute);
        }

        FileUtils.copyFileToDirectory(fileFrom, fileTo);

        return String.format("File '%s' was copied to '%s'", getRelativePath(), getRelativePath(transformedAppFolder, fileTo));
    }

}