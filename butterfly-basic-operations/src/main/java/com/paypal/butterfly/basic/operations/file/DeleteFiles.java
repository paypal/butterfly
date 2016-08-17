package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Operation for multiple files deletion
 * <strong>Important notes:</strong>
 * <ol>
 *     <li>The term "file" here refers to both files and folders</li>
 *     <li>Different than other {@link TransformationOperation}, setting an absolute or relative path is not necessary, since the files this operation will perform against are set by a transformation context attribute</li>
 * </ol>
 *
 * @author facarvalho
 */
public class DeleteFiles extends TransformationOperation<DeleteFiles> {

    private static final String DESCRIPTION = "Delete all files listed under transformation context attribute %s";

    // Name of transformation context attribute that holds
    // all files to be deleted
    private String attributeName;

    public DeleteFiles() {
        relative(".");
    }

    /**
     * Operation for multiple files deletion
     * <strong>Important: the term files here refers to both file and folders</strong>
     *
     * @param attributeName name of transformation context attribute that holds
     *                      all files to be deleted
     */
    public DeleteFiles(String attributeName) {
        this();
        setAttributeName(attributeName);
    }

    /**
     * Set the name of transformation context attribute that holds
     * all files to be deleted
     *
     * @param attributeName name of transformation context attribute that holds
     *                      all files to be deleted
     * @return this transformation operation object
     */
    public DeleteFiles setAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * Return the name of transformation context attribute that holds
     * all files to be deleted
     *
     * @return the name of transformation context attribute that holds
     * all files to be deleted
     */
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, attributeName);
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO
        // Take a look at this: https://docs.oracle.com/javase/tutorial/essential/io/walk.html

        List<File> filesToBeDeleted = (List<File>) transformationContext.get(attributeName);
        for(File fileToBeDeleted : filesToBeDeleted) {
            FileUtils.forceDelete(fileToBeDeleted);
        }

        // TODO improve this message to state relative paths, instead of absolute
        return String.format("Deleted files: %s", filesToBeDeleted.toString());
    }

    @Override
    public DeleteFiles clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}