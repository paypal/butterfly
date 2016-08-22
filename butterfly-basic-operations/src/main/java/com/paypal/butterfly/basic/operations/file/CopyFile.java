package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Operation to copy a file. The relative or absolute file is the
 * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
 * or {@link #setToAbsolute(String)}
 * </br>
 * <strong>Note:<strong/> if you want to copy a set of specific files from one
 * location to another, then use a multiple transformation operation
 * (see {@code TransformationTemplate.addMultiple()}) with {@link CopyFile}. Now, if
 * you want to copy a directory and its content from one location to another, then
 * use {@link CopyDirectory} instead.
 *
 * @author facarvalho
 */
public class CopyFile extends AbstractCopy<CopyFile> {

    private static final String DESCRIPTION = "Copy file %s to %s";

    /**
     * Operation to copy a file. The relative or absolute file is the
     * "from" file, while the "to" location is specified via {@link #setToRelative(String)}
     * or {@link #setToAbsolute(String)}
     */
    public CopyFile() {
        super(DESCRIPTION);
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

    @Override
    public CopyFile clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}