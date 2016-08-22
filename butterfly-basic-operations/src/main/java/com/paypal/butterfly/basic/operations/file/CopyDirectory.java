package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.io.FileUtils;

import java.io.File;

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
public class CopyDirectory extends AbstractCopy<CopyDirectory> {

    private static final String DESCRIPTION = "Copy a directory and its content from %s to %s";

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
        super(DESCRIPTION);
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