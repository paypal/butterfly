package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transformation operation
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<TO> {

    private static final String OPERATION_NAME_SYNTAX = "%s-%d-%s";

    // This transformation operation instance name
    private String name;

    // The execution order for this operation on its template
    // -1 means it has not been registered to any template yet
    // 1 means first
    private int order = -1;

    // Relative path from the application root folder to the file or
    // folder the operation should perform against
    private String relativePath;

    // Indicates whether or not this operation has already been
    // executed. Transformation operations are supposed to
    // be executed ONLY ONCE
    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);

    // Abort the whole transformation if this operation fails
    private boolean abortOnFailure = true;
    private TransformationTemplate template;

    /**
     * @see {@link #setRelativePath(String)}
     *
     * @param relativePath
     */
    protected TransformationOperation(String relativePath) {
        setRelativePath(relativePath);
    }

    /**
     * Returns a short one line, but SPECIFIC, description about the transformation
     * operation, including mentioning the files and/or folders
     * to be manipulated. This is supposed to be an one line statement about the
     * specific operation that was executed. This would be used for example in
     * log statements or user interfaces.
     *
     * @return a short one line, but specific, description about the transformation
     * operation
     */
    public abstract String getDescription();

    /**
     * Returns an explanation of the criteria to pass or fail pre-execution validation
     * for this transformation operation.
     * </br>
     * You MUST override this method if the operation has a pre-execution validation check.
     *
     * @return an explanation of the criteria to pass or fail pre-execution validation,
     *  or null, in case this operation has no pre-execution validation
     */
    public String getPreValidationExplanation() {
        return null;
    }

    /**
     * Returns an explanation of the criteria to pass or fail post-execution validation
     * for this transformation operation.
     * </br>
     * You MUST override this method if the operation has a post-execution validation check.
     *
     * @return an explanation of the criteria to pass or fail post-execution validation,
     *  or null, in case this operation has no post-execution validation
     */
    public String getPostValidationExplanation() {
        return null;
    }

    /**
     * Sets the relative path from the application root folder
     * to the file or folder the operation should perform against.
     * Three options are valid when separating folders in the path:
     * <ol>
     * <li>1-File.separatorChar (e.g. setRelativePath("myFolder" + File.separator + "file.txt")</li>
     * <li>2-Forward slash (e.g. setRelativePath("myFolder/file.txt")</li>
     * <li>3-Two backward slashes (e.g. setRelativePath("myFolder\\file.txt")</li>
     * </ol>
     * The slashes are replaced by OS specific separator char in runtime.
     *
     * @param relativePath from the application root folder
     *  to the file or folder the operation should be performed against
     */
    public final TO setRelativePath(String relativePath) {
        this.relativePath = relativePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        return (TO) this;
    }

    /**
     * Returns relative path (from the application root folder) to the
     * file or folder the transformation operation is suppose to perform against
     *
     * @return
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
     * Performs the transformation operation against
     * the application to be transformed
     *
     * @param transformedAppFolder
     * @return a message stating with details the operation that has been performed
     */
    public final synchronized String perform(File transformedAppFolder) throws TransformationOperationException {
        if(hasBeenPerformed.get()) {
            throw new IllegalStateException("This transformation operation has already been performed");
        }
        if(!preExecutionValidation(transformedAppFolder)) {
            throw new TransformationOperationException(name + " pre-execution validation has failed");
            // TODO create a meta-data here with reasons for pre-validation failures, it should state the error
        }

        // TODO result should be a composite type, including potential warnings
        String resultMessage;
        try {
            resultMessage = execution(transformedAppFolder);
        } catch(Exception e) {
            throw new TransformationOperationException(name + " has failed", e);
        }

        hasBeenPerformed.set(true);

        // TODO post validation handling

        return resultMessage;
    }

    /**
     * The implementation of this transformation operation
     *
     * @param transformedAppFolder
     *
     * @return an specific status message about the operation
     * execution result. This message should state clearly, but shortly, the actual file
     * being changed, and what change had been done. The file should be referred
     * by its relative path (see {@link #getRelativePath()})
     */
	 // TODO return stype should be something that state a successfull message,
	 // but also warnings, if they happen
    protected abstract String execution(File transformedAppFolder) throws Exception;

    /**
     *
     * @return true if this operation has already been performed
     */
    public final boolean hasBeenPerformed() {
        return hasBeenPerformed.get();
    }

    /**
     * Return true only if this operation's pre-req has been met. If this returns
     * false, the operation will NOT be executed, and the whole transformation
     * might be aborted, depending on {@link #abortTransformationOnFailure}
     *
     * @param transformedAppFolder
     *
     * @return
     */
    protected boolean preExecutionValidation(File transformedAppFolder) {
        // TODO implement this logic in perform method
        // TODO create a meta-data here with reasons for pre-validation failures
        return true;
    }

    /**
     * Return true only if this operation's post-execution check has succeeded.
     * If this returns false, the operation WILL NOT be rolled back, but a
     * warning will be stated, and the whole transformation might be aborted,
     * depending on {@link #abortTransformationOnFailure}
     *
     * @param transformedAppFolder
     *
     * @return
     */
    protected boolean postExecutionValidation(File transformedAppFolder) {
        // TODO implement this logic in perform method
        // TODO create a meta-data here with reasons for post-validation failures
        return true;
    }

    /**
     * If set to true, abort the whole transformation if validation or execution fails.
     * If not, just state a warning, aborts the operation execution only.
     *
     * @param abort
     * @return
     */
    protected TO abortTransformationOnFailure(boolean abort) {
        // TODO implement this logic in perform method
        abortOnFailure = abort;
        return (TO) this;
    }

    /**
     * Returns whether this operation aborts the transformation or not in
     * case of an operation failure. Notice that this method does NOT
     * change the state this object in any ways, it is just a getter.
     *
     * @return true only if this operation aborts the transformation or not in
     * case of an operation failure
     */
    public boolean abortTransformationOnFailure() {
        return abortOnFailure;
    }

    /**
     * Register this operation to a template, and also assign it a name
     * based on the template name and order of execution
     *
     * @param template
     * @param order
     */
    final void setTemplate(TransformationTemplate template, int order) {
        this.template = template;
        this.order = order;

        if(name == null) {
            setName(String.format(OPERATION_NAME_SYNTAX, template.getName(), order, ((TO) this).getClass().getSimpleName()));
        }
    }

    /**
     * Returns the transformation template this operation instance belongs to
     *
     * @return the transformation template this operation instance belongs to
     */
    public TransformationTemplate getTemplate() {
        return template;
    }

    /**
     * Set this transformation operation instance name.
     * If not set, a default name will be assigned at the
     * time it is added to a template.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

}
