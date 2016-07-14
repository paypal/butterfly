package com.paypal.butterfly.extensions.api;

import java.io.File;

/**
 * Condition to determine if a transformation operation
 * should be executed or not
 *
 * IMPORTANT:
 * Every TransformationOperationCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * TransformationOperationCondition instance.
 *
 * @author facarvalho
 */
public abstract class TransformationOperationCondition<TOC> {

    private String relativePath = "";

    public TransformationOperationCondition() {
    }

    /**
     * Returns relative path (from the application root folder) to the
     * file or folder the operation condition should check
     *
     * @return relative path (from the application root folder) to the
     * file or folder the operation condition should check
     */
    protected final String getRelativePath() {
        return relativePath;
    }

    /**
     * Returns an absolute path to the file or folder the transformation
     * operation is suppose to perform against
     *
     * @param transformedAppFolder
     * @return
     */
    protected final File getAbsoluteFile(File transformedAppFolder) {
        return new File(transformedAppFolder, getRelativePath());
    }

    /**
     * Sets the relative path from the application root folder
     * to the file or folder the operation condition should check.
     * Three options are valid when separating folders in the path:
     * <ol>
     * <li>1-File.separatorChar (e.g. setRelativePath("myFolder" + File.separator + "file.txt")</li>
     * <li>2-Forward slash (e.g. setRelativePath("myFolder/file.txt")</li>
     * <li>3-Two backward slashes (e.g. setRelativePath("myFolder\\file.txt")</li>
     * </ol>
     * The slashes are replaced by OS specific separator char in runtime.
     *
     * @param relativePath from the application root folder
     *  to the file or folder the operation should check.
     */
    public final TOC setRelativePath(String relativePath) {
        this.relativePath = relativePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        return (TOC) this;
    }

    /**
     * Returns true only if this condition is true,
     * which will result in letting the operation
     * be executed. If an exception is thrown during
     * evaluation, it is recommended that evaluation
     * fails, and the exception is logged as error
     *
     * @param transformedAppFolder
     * @return true only if this condition is true
     */
    protected abstract boolean evaluate(File transformedAppFolder);

    /**
     * Returns a short one line specific description about the condition to execute the
     * operation. It MUST mention the files and/or folders to be evaluated.
     *
     * @return a short one line specific description about the condition to execute the
     * operation
     */
    public abstract String getDescription();

    @Override
    public String toString() {
        return getDescription();
    }

}
