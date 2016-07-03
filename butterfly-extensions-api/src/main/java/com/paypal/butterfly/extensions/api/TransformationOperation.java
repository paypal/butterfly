package com.paypal.butterfly.extensions.api;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<T> {

    private String relativePath;

    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);

    /**
     * Returns a generic description about the transformation
     * operation
     *
     * @return a generic description about the transformation
     * operation
     */
    public abstract String getDescription();

    /**
     * Returns the relative path from the application root folder
     * to the file or folder the operation should perform against.
     * The File path separator should be based on
     * {@link java.io.File.separator}
     *
     * @param relativePath from the application root folder
     *  to the file or folder the operation shoudl be performed against
     */
    public final T setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        return (T) this;
    }

    protected final String getRelativePath() {
        return relativePath;
    }

    /**
     * Performs the transformation operation against
     * the application to be transformed
     *
     * @param transformedAppFolder
     * @return a message stating with details the operation that has been performed
     */
    public final synchronized String perform(File transformedAppFolder) {
        if(hasBeenPerformed.get()) {
            throw new IllegalStateException("This transformation operation has already been performed");
        }

        String resultMessage = execution(transformedAppFolder);

        hasBeenPerformed.set(true);

        return resultMessage;
    }

    /**
     * The implementation of this transformation operation
     *
     * @param transformedAppFolder
     * @return an specific status message about the operation
     * execution result
     */
    protected abstract String execution(File transformedAppFolder);

    /**
     *
     * @return true if this operation has already been performed
     */
    public final boolean hasBeenPerformed() {
        return hasBeenPerformed.get();
    }

}
