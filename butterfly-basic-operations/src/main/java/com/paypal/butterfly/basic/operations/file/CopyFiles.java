package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Operation to copy multiple files from one location to another.
 * </br>
 * There are two methods to define the set of files to be copied:
 * <ol>
 *  <li>Direct copy: files to be copied are defined via a {@code List<File>} transformation context attribute, usually set previously by a {@link com.paypal.butterfly.basic.utilities.file.FindFiles}. In this case, the path to the files to be copied are ignored, and all of them are placed directly into the destination location. This method is used whenever {@link #setFromAttribute(String)} is set.</li>
 *  <li>Content copy: files to be copied are all the content, including sub-folders and their files, coming rom relative or absolute location. In this case, the path to the files to be copied from the absolute location are preserved, and those folders are also copied to the destination location. This method is used whenever {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} are set, unless {@link #setFromAttribute(String)} was also set.</li>
 * </ol>
 * <strong>Note:</strong>Direct copy has higher precedence, so, whenever {@link #setFromAttribute(String)} is set, direct copy method will be used.
 *
 * @author facarvalho
 */
public class CopyFiles extends TransformationOperation<CopyFiles> {

    private static final String DESCRIPTION = "Copy files set by transformation context attribute %s to %s";

    // Name of the transformation context attribute that holds
    // a list of files to be copied
    private String fromAttribute;

    // Relative location where to copy the file to
    private String toRelative = null;

    // Name of the transformation context attribute that holds
    // the absolute location where to copy the file to
    // Only one should be set between this and {@code toRelative}
    private String toAbsoluteAttribute = null;

    /**
     * Operation to copy multiple files from one location to another.
     * </br>
     * There are two methods to define the set of files to be copied:
     * <ol>
     *  <li>From a transformation context attribute. In this case the path to the files to be copied are ignored, and all of them are placed directly into the destination location. This method is used whenever {@link #setFromAttribute(String)} is called.</li>
     *  <li>From relative or absolute. In this case the path to the files to be copied from the absolute location are preserved, and those folders are also copied to the destination location. This method is used whenever {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} are called.</li>
     * </ol>
     */
    public CopyFiles() {
    }

    /**
     * Set the name of the transformation context attribute that holds
     * a list of files to be copied
     *
     * @param fromAttribute Name of the transformation context attribute that holds
     *                      a list of files to be copied
     */
    public CopyFiles setFromAttribute(String fromAttribute) {
        this.fromAttribute = fromAttribute;
        return this;
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
    public CopyFiles setToRelative(String toRelative) {
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
    public CopyFiles setToAbsolute(String attributeName) {
        this.toAbsoluteAttribute = attributeName;
        this.toRelative = null;
        return this;
    }

    public String getFromAttribute() {
        return fromAttribute;
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
        String result = null;
        if(fromAttribute != null) {
            result = directCopy(transformedAppFolder, transformationContext);

        // TODO Validation must be done here!!! In case none has been set!

        } else {
            result = contentCopy(transformedAppFolder, transformationContext);
        }
        return result;
    }

    private String directCopy(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        List<File> filesFrom = (List<File>) transformationContext.get(fromAttribute);
        File fileTo = getFileTo(transformedAppFolder, transformationContext);
        for(File fileFrom : filesFrom) {
            FileUtils.copyFileToDirectory(fileFrom, fileTo);
        }

        // TODO this message is wrong, list of files printed should be relative
        return String.format("Files '%s' copied to '%s'", filesFrom, getRelativePath(transformedAppFolder, fileTo));
    }

    private String contentCopy(File transformedAppFolder, TransformationContext transformationContext) throws IOException {
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

}